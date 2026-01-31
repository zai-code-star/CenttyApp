package com.example.admin.ui.listing

import android.os.Bundle
import android.widget.EditText
import android.widget.Button
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.example.admin.R
import com.google.firebase.database.*

class EditActivity : AppCompatActivity() {
    private lateinit var databaseReference: DatabaseReference
    private lateinit var itemUid: String
    private lateinit var itemNameEditText: EditText
    private lateinit var itemPriceEditText: EditText
    private lateinit var alamatedittext: EditText
    private lateinit var spesifikasiedittext: EditText
    private lateinit var itemType: String // Menyimpan jenis item

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_edit)

        // Initialize Firebase Database Reference
        databaseReference = FirebaseDatabase.getInstance().getReference("postings")

        // Mendapatkan UID dan jenis item dari intent
        itemUid = intent.getStringExtra("ITEM_UID") ?: ""
        itemType = intent.getStringExtra("ITEM_TYPE") ?: "" // Mendapatkan jenis item

        // Initialize views
        itemNameEditText = findViewById(R.id.editTextItemName)
        itemPriceEditText = findViewById(R.id.editTextItemPrice)
        alamatedittext = findViewById(R.id.alamat)
        spesifikasiedittext = findViewById(R.id.spesifikasi)
        // Inisialisasi EditText lainnya sesuai dengan ID layout

        // Mendapatkan data dari Firebase dan menampilkannya pada UI
        retrieveDataFromFirebase(itemUid)

        // Set click listener for update button
        findViewById<Button>(R.id.btn_update).setOnClickListener {
            updateItem()
        }
    }

    private fun retrieveDataFromFirebase(itemUid: String) {
        databaseReference.child(itemType).child(itemUid).get().addOnSuccessListener { snapshot ->
            if (snapshot.exists()) {
                val itemName = snapshot.child("nama").getValue(String::class.java) ?: ""
                val itemPrice = snapshot.child("harga").getValue(Double::class.java) ?: 0.0
                val alamat = snapshot.child("alamat").getValue(String::class.java) ?: ""
                val spesifikasi = snapshot.child("spesifikasi").getValue(String::class.java) ?: ""

                // Ubah harga menjadi format dengan pemisah ribuan
                val formattedPrice = String.format("%,.0f", itemPrice)

                // Tampilkan data pada EditText
                itemNameEditText.setText(itemName)
                itemPriceEditText.setText(formattedPrice)
                alamatedittext.setText(alamat)
                spesifikasiedittext.setText(spesifikasi)
                // Tampilkan data lainnya pada EditText sesuai kebutuhan
            }
        }.addOnFailureListener { exception ->
            // Handle failure
        }
    }

    private fun updateItem() {
        val newName = itemNameEditText.text.toString()
        val newPriceText = itemPriceEditText.text.toString()
        val newAlamat = alamatedittext.text.toString()
        val newSpesifikasi = spesifikasiedittext.text.toString()

        // Hapus karakter selain angka dan titik desimal
        val cleanedPriceText = newPriceText.replace(Regex("[^\\d.]"), "")

        try {
            // Validasi jumlah desimal
            val decimalCount = cleanedPriceText.substringAfterLast('.', "").length
            if (decimalCount > 3) {
                throw NumberFormatException("Invalid format: Maximum three decimal places allowed")
            }

            // Konversi ke tipe data Double setelah validasi
            val newPrice = cleanedPriceText.toDouble()

            // Update data di Firebase
            val updateData = hashMapOf<String, Any>(
                "nama" to newName,
                "harga" to newPrice,
                "alamat" to newAlamat,
                "spesifikasi" to newSpesifikasi
                // Tambahkan data lainnya sesuai kebutuhan
            )

            // Update data sesuai dengan jenis item
            databaseReference.child(itemType).child(itemUid).updateChildren(updateData)
                .addOnSuccessListener {
                    // Handle keberhasilan penyimpanan
                    finish() // Tutup activity setelah update berhasil
                }
                .addOnFailureListener { exception ->
                    // Handle kegagalan penyimpanan
                    Toast.makeText(
                        this,
                        "Failed to save changes: ${exception.message}",
                        Toast.LENGTH_SHORT
                    ).show()
                }
        } catch (e: NumberFormatException) {
            // Tangani kesalahan format angka
            Toast.makeText(
                this,
                "Invalid price: Please make sure to enter a valid price",
                Toast.LENGTH_SHORT
            ).show()
        }
    }
}