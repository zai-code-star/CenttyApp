package com.example.user.ui.dashboard

import android.os.Bundle
import android.util.Log
import android.widget.Button
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.viewpager2.widget.ViewPager2
import com.example.user.R
import com.example.user.data.model.ListItem
import com.example.user.ui.search.ImageSliderAdapter
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.DatabaseReference
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener
import java.util.*

class DetailItem : AppCompatActivity() {
    private lateinit var itemUid: String
    private lateinit var itemImages: ArrayList<String>
    private lateinit var itemType: String
    private lateinit var auth: FirebaseAuth
    private lateinit var databaseReference: DatabaseReference

    private lateinit var itemNameTextView: TextView
    private lateinit var itemPriceTextView: TextView
    private lateinit var tipePropertiTextView: TextView
    private lateinit var alamatTextView: TextView
    private lateinit var kecamatanTextView: TextView
    private lateinit var spesifikasiTextView: TextView
    private lateinit var viewPager: ViewPager2
    private lateinit var favoriteButton: Button

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_detail)

        auth = FirebaseAuth.getInstance()
        databaseReference = FirebaseDatabase.getInstance().getReference("postings")

        itemUid = intent.getStringExtra("ITEM_UID") ?: ""
        this.itemType = intent.getStringExtra("ITEM_TYPE") ?: ""

        itemNameTextView = findViewById(R.id.item_name_textview)
        itemPriceTextView = findViewById(R.id.item_price_textview)
        tipePropertiTextView = findViewById(R.id.tipeproperti)
        alamatTextView = findViewById(R.id.alamat)
        kecamatanTextView = findViewById(R.id.kecamatan)
        spesifikasiTextView = findViewById(R.id.spesifikasi)
        viewPager = findViewById(R.id.viewPager)
        favoriteButton = findViewById(R.id.favorite_button)

        Log.d("DetailItem", "Item UID: $itemUid")
        Log.d("DetailItem", "Item Type: $itemType")

        retrieveDataFromFirebase(itemUid)

        favoriteButton.setOnClickListener {
            addToFavorites(itemUid, itemType)
        }
    }

    private fun addToFavorites(itemUid: String, itemType: String) {
        val currentUser = auth.currentUser
        currentUser?.let { user ->
            val favoritesRef =
                FirebaseDatabase.getInstance().getReference("favorites").child(user.uid)
                    .child(itemType).child(itemUid)
            favoritesRef.setValue(true)
                .addOnSuccessListener {
                    showToast("Item telah ditambahkan ke Favorit")
                }
                .addOnFailureListener {
                    showToast("Gagal menambahkan item ke Favorit")
                }
        }
    }

    private fun showToast(message: String) {
        Toast.makeText(this, message, Toast.LENGTH_SHORT).show()
    }

    private fun retrieveDataFromFirebase(itemUid: String) {
        // Mendengarkan perubahan pada seluruh daftar postingan
        databaseReference.addListenerForSingleValueEvent(object : ValueEventListener {
            override fun onDataChange(dataSnapshot: DataSnapshot) {
                if (dataSnapshot.exists()) {
                    // Iterasi melalui setiap postingan dalam daftar
                    for (childSnapshot in dataSnapshot.children) {
                        // Memeriksa apakah postingan saat ini memiliki child dengan UID yang sesuai
                        if (childSnapshot.hasChild(itemUid)) {
                            // Mendapatkan jenis item (jual/sewa)
                            val itemType = childSnapshot.key
                            // Mendapatkan referensi ke postingan dengan UID yang sesuai
                            val itemQuery = childSnapshot.child(itemUid)

                            // Mengekstrak data dari snapshot dan memasukkannya ke dalam objek ListItem
                            itemQuery.getValue(ListItem::class.java)?.let { item ->
                                // Memperbarui UID item dengan UID yang benar
                                item.uid = itemQuery.key ?: ""

                                // Set data ke dalam TextViews
                                itemNameTextView.text = item.nama
                                tipePropertiTextView.text = "TipeProperti: ${item.tipeProperti}"
                                alamatTextView.text = "Alamat: ${item.alamat}"
                                kecamatanTextView.text = "Kec: ${item.kecamatan}"
                                spesifikasiTextView.text = "Spesifikasi: ${item.spesifikasi}"

                                // Format harga dan set ke dalam TextView
                                val formattedHarga = String.format("%,.0f", item.harga)
                                itemPriceTextView.text = "Harga: $formattedHarga"

                                // Mengisi list gambar dan mengatur adapter ViewPager
                                itemImages = ArrayList(item.uriGambar ?: emptyList())
                                val adapter = ImageSliderAdapter(itemImages)
                                viewPager.adapter = adapter

                                // Timer untuk menggeser gambar dalam ViewPager
                                val timer = Timer()
                                timer.schedule(object : TimerTask() {
                                    override fun run() {
                                        runOnUiThread {
                                            if (viewPager.currentItem < adapter.itemCount - 1) {
                                                viewPager.currentItem = viewPager.currentItem + 1
                                            } else {
                                                viewPager.currentItem = 0
                                            }
                                        }
                                    }
                                }, 3000, 3000)
                            }
                            // Keluar dari loop setelah menemukan item
                            break
                        }
                    }
                } else {
                    // Handle jika tidak ada data di bawah 'postings'
                    Log.d("DetailItem", "No data found under 'postings'")
                }
            }

            override fun onCancelled(databaseError: DatabaseError) {
                // Handle kesalahan saat mengambil data dari Firebase
                Log.e("DetailItem", "Error retrieving data from Firebase: ${databaseError.message}")
            }
        })
    }
}