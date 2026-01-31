package com.example.user.data.repository

import com.example.user.data.model.ListItem
import com.google.firebase.database.*

class ListingRepository {
    private val databaseReference: DatabaseReference by lazy {
        FirebaseDatabase.getInstance().getReference("postings")
    }

    fun getDataFromFirebaseByType(tipe: String, callback: (List<ListItem>) -> Unit) {
        val items = mutableListOf<ListItem>()
        val query = databaseReference.child(tipe) // Mengambil referensi berdasarkan jenis tipe

        query.addValueEventListener(object : ValueEventListener {
            override fun onDataChange(dataSnapshot: DataSnapshot) {
                items.clear()
                for (snapshot in dataSnapshot.children) {
                    val uid = snapshot.key ?: "" // Mendapatkan UID dari setiap entri
                    val nama = snapshot.child("nama").getValue(String::class.java) ?: ""
                    val harga = snapshot.child("harga").getValue(Double::class.java) ?: 0.0
                    val alamat = snapshot.child("alamat").getValue(String::class.java) ?: ""
                    val kecamatan = snapshot.child("kecamatan").getValue(String::class.java)
                        ?: "" // Mengambil nilai kecamatan
                    val tipeProperti = snapshot.child("tipeProperti").getValue(String::class.java)
                        ?: "" // Mengambil nilai tipeProperti
                    val spesifikasi = snapshot.child("spesifikasi").getValue(String::class.java)
                        ?: "" // Mengambil nilai spesifikasi

                    // Perhatikan pengambilan uriGambar di sini
                    val uriGambarList = mutableListOf<String>()
                    snapshot.child("uriGambar").children.forEach { uriSnapshot ->
                        val uriGambar = uriSnapshot.getValue(String::class.java)
                        uriGambar?.let {
                            uriGambarList.add(it)
                        }
                    }

                    val listItem = ListItem(
                        uid,
                        nama,
                        harga,
                        alamat,
                        kecamatan,
                        tipeProperti,
                        spesifikasi,
                        tipe,
                        uriGambarList
                    )
                    items.add(listItem)
                }
                callback(items)
            }

            override fun onCancelled(databaseError: DatabaseError) {
                // Handle error jika diperlukan
            }
        })
    }

    fun getAllDataFromFirebase(callback: (List<ListItem>) -> Unit) {
        val items = mutableListOf<ListItem>()

        databaseReference.addValueEventListener(object : ValueEventListener {
            override fun onDataChange(dataSnapshot: DataSnapshot) {
                items.clear()
                for (snapshot in dataSnapshot.children) {
                    val tipe = snapshot.key ?: ""
                    snapshot.children.forEach { itemSnapshot ->
                        val uid = itemSnapshot.key ?: ""
                        val nama = itemSnapshot.child("nama").getValue(String::class.java) ?: ""
                        val harga = itemSnapshot.child("harga").getValue(Double::class.java) ?: 0.0
                        val alamat = itemSnapshot.child("alamat").getValue(String::class.java) ?: ""
                        val kecamatan = itemSnapshot.child("kecamatan").getValue(String::class.java)
                            ?: "" // Mengambil nilai kecamatan
                        val tipeProperti =
                            itemSnapshot.child("tipeProperti").getValue(String::class.java)
                                ?: "" // Mengambil nilai tipeProperti
                        val spesifikasi =
                            itemSnapshot.child("spesifikasi").getValue(String::class.java)
                                ?: "" // Mengambil nilai spesifikasi

                        val uriGambarList = mutableListOf<String>()
                        itemSnapshot.child("uriGambar").children.forEach { uriSnapshot ->
                            val uriGambar = uriSnapshot.getValue(String::class.java)
                            uriGambar?.let {
                                uriGambarList.add(it)
                            }
                        }

                        val listItem = ListItem(
                            uid,
                            nama,
                            harga,
                            alamat,
                            kecamatan,
                            tipeProperti,
                            spesifikasi,
                            tipe,
                            uriGambarList
                        )
                        items.add(listItem)

                    }
                }
                callback(items)
            }

            override fun onCancelled(databaseError: DatabaseError) {
                // Handle error jika diperlukan
            }
        })
    }
}

