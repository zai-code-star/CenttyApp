package com.example.admin.ui.listing

import android.content.Intent
import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import com.example.admin.R
import android.widget.TextView
import androidx.viewpager2.widget.ViewPager2
import com.example.admin.data.model.ListItem
import com.google.firebase.database.*
import java.util.Timer
import java.util.TimerTask
import android.widget.Button
import android.widget.Toast
import androidx.appcompat.app.AlertDialog
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout

class DetailActivity : AppCompatActivity() {
    private lateinit var databaseReference: DatabaseReference
    private lateinit var itemUid: String
    private lateinit var itemImages: ArrayList<String>
    private lateinit var swipeRefreshLayout: SwipeRefreshLayout
    private lateinit var itemType: String // Tambahkan variabel itemType

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_detail)

        // Initialize Firebase Database Reference
        databaseReference = FirebaseDatabase.getInstance().getReference("postings")

        // Mendapatkan UID dan jenis item dari intent
        itemUid = intent.getStringExtra("ITEM_UID") ?: ""
        this.itemType = intent.getStringExtra("ITEM_TYPE") ?: "" // Inisialisasi properti kelas


        // Retrieve data dari Firebase berdasarkan UID dan jenis item
        retrieveDataFromFirebase(itemUid, itemType)

        // Set click listener for the update button
        findViewById<Button>(R.id.update).setOnClickListener {
            val intent = Intent(this, EditActivity::class.java)
            intent.putExtra("ITEM_UID", itemUid)
            intent.putExtra("ITEM_TYPE", itemType) // Tambahkan jenis item ke intent
            startActivity(intent)
        }


        // Set tombol delete click listener
        val deleteButton: Button = findViewById(R.id.delete)
        deleteButton.setOnClickListener {
            // Panggil metode untuk menghapus item dari Firebase
            deleteItemFromFirebase(itemUid)
        }

        // Initialize SwipeRefreshLayout
        swipeRefreshLayout = findViewById(R.id.swipe_refresh_layout)

        // Set listener for SwipeRefreshLayout
        swipeRefreshLayout.setOnRefreshListener {
            // Panggil fungsi untuk meretrieve data dari Firebase
            retrieveDataFromFirebase(itemUid, itemType)
            // Berhenti menampilkan indikator refresh setelah selesai
            swipeRefreshLayout.isRefreshing = false
        }
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


                        // Ubah harga menjadi format dengan pemisah ribuan
                        val formattedHarga = String.format("%,.0f", item.harga)
                        itemPriceTextView.text = "Harga: $formattedHarga"

                        itemImages = ArrayList(item.uriGambar ?: emptyList())

                        // Menampilkan gambar pada UI menggunakan ViewPager2
                        val viewPager: ViewPager2 = findViewById(R.id.viewPager)
                        val adapter = ImageSliderAdapter(itemImages)
                        viewPager.adapter = adapter

                        // Membuat timer untuk auto scroll
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
                        }, 3000, 3000) // Auto scroll setiap 3 detik
                    }
                }

                override fun onCancelled(databaseError: DatabaseError) {
                    // Handle error jika diperlukan
                }
            })
    }


    // Method untuk menghapus item dari Firebase
    private fun deleteItemFromFirebase(uid: String) {
        val confirmationDialog = AlertDialog.Builder(this)
            .setTitle("Konfirmasi Penghapusan")
            .setMessage("Apakah Anda yakin ingin menghapus item ini?")
            .setPositiveButton("Ya") { dialog, _ ->
                // Hapus item dari child yang sesuai dengan jenis item
                databaseReference.child(this.itemType).child(uid).removeValue()
                    .addOnSuccessListener {
                        // Item berhasil dihapus, kembali ke ListingFragment
                        Toast.makeText(this, "Item berhasil dihapus", Toast.LENGTH_SHORT).show()
                        finish()
                    }
                    .addOnFailureListener { e ->
                        // Gagal menghapus item, tampilkan pesan kesalahan jika perlu
                        Toast.makeText(
                            this,
                            "Gagal menghapus item: ${e.message}",
                            Toast.LENGTH_SHORT
                        ).show()
                    }
                dialog.dismiss()
            }
            .setNegativeButton("Tidak") { dialog, _ ->
                dialog.dismiss()
            }
            .create()

        confirmationDialog.show()
    }
}
