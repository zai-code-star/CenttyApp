package com.example.user.ui.search

import android.os.Bundle
import android.util.Log
import android.widget.Button
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout
import androidx.viewpager2.widget.ViewPager2
import com.example.user.R
import com.example.user.data.model.ListItem
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.*
import java.util.*

class DetailActivity : AppCompatActivity() {
    private lateinit var databaseReference: DatabaseReference
    private lateinit var itemUid: String
    private lateinit var itemImages: ArrayList<String>
    private lateinit var swipeRefreshLayout: SwipeRefreshLayout
    private lateinit var itemType: String
    private lateinit var favoriteButton: Button

    private lateinit var auth: FirebaseAuth

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_detail)

        auth = FirebaseAuth.getInstance()
        databaseReference = FirebaseDatabase.getInstance().getReference("postings")

        itemUid = intent.getStringExtra("ITEM_UID") ?: ""
        this.itemType = intent.getStringExtra("ITEM_TYPE") ?: ""

        Log.d("DetailItem", "Item UID: $itemUid")
        Log.d("DetailItem", "Item Type: $itemType")

        retrieveDataFromFirebase(itemUid, itemType)

        swipeRefreshLayout = findViewById(R.id.swipe_refresh_layout)

        swipeRefreshLayout.setOnRefreshListener {
            retrieveDataFromFirebase(itemUid, itemType)
            swipeRefreshLayout.isRefreshing = false
        }

        favoriteButton = findViewById(R.id.favorite_button)
        favoriteButton.setOnClickListener {
            addToFavorites(itemUid, itemType)
            showToast("Item telah ditambahkan ke Favorit")
            // Tambahkan baris berikut untuk memperbarui tampilan setelah item ditambahkan ke favorit
            favoriteButton.isEnabled = false // Tidak dapat menambahkan lagi setelah ditambahkan ke favorit

        }
    }

    private fun addToFavorites(itemUid: String, itemType: String) {
        val currentUser = auth.currentUser
        currentUser?.let { user ->
            val favoritesRef = FirebaseDatabase.getInstance().getReference("favorites").child(user.uid).child(itemType).child(itemUid)
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

    private fun retrieveDataFromFirebase(itemUid: String, itemType: String) {
        databaseReference.child(itemType).child(itemUid)
            .addListenerForSingleValueEvent(object : ValueEventListener {
                override fun onDataChange(dataSnapshot: DataSnapshot) {
                    val item = dataSnapshot.getValue(ListItem::class.java)
                    item?.let {
                        val itemNameTextView: TextView = findViewById(R.id.item_name_textview)
                        val itemPriceTextView: TextView = findViewById(R.id.item_price_textview)
                        val tipeproperti: TextView = findViewById(R.id.tipeproperti)
                        val alamat: TextView = findViewById(R.id.alamat)
                        val kecamatan: TextView = findViewById(R.id.kecamatan)
                        val spesifikasi: TextView = findViewById(R.id.spesifikasi)

                        itemNameTextView.text = "${item.nama}"
                        tipeproperti.text = "TipeProperti: ${item.tipeProperti}"
                        alamat.text = "Alamat: ${item.alamat}"
                        kecamatan.text = "Kec: ${item.kecamatan}"
                        spesifikasi.text = "Spesifikasi: ${item.spesifikasi}"

                        val formattedHarga = String.format("%,.0f", item.harga)
                        itemPriceTextView.text = "Harga: $formattedHarga"

                        itemImages = ArrayList(item.uriGambar ?: emptyList())

                        val viewPager: ViewPager2 = findViewById(R.id.viewPager)
                        val adapter = ImageSliderAdapter(itemImages)
                        viewPager.adapter = adapter

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
                }

                override fun onCancelled(databaseError: DatabaseError) {
                }
            })
    }
}
