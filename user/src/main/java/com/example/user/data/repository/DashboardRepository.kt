package com.example.user.data.repository

import android.util.Log
import com.example.user.data.model.DashboardItem
import com.example.user.data.model.ListItem
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseUser
import com.google.firebase.database.*

class DashboardRepository {
    private val currentUser: FirebaseUser
    private val databaseReference: DatabaseReference by lazy {
        FirebaseDatabase.getInstance().getReference("postings")
    }

    init {
        // Get current user from Firebase Authentication
        val firebaseAuth = FirebaseAuth.getInstance()
        currentUser = firebaseAuth.currentUser!!
    }

    fun getDashboardItemsByType(
        tipeProperti: String,
        callback: (List<DashboardItem>) -> Unit
    ) {
        val jualQuery = databaseReference.child("Jual")
            .orderByChild("tipeProperti")
            .equalTo(tipeProperti)

        val sewaQuery = databaseReference.child("Sewa")
            .orderByChild("tipeProperti")
            .equalTo(tipeProperti)

        val items = mutableListOf<DashboardItem>()

        var count = 0
        val totalCount = 2 // Total jumlah query yang akan dieksekusi

        val mergedListener = object : ValueEventListener {
            override fun onDataChange(snapshot: DataSnapshot) {
                Log.d("DashboardRepository", "onDataChange called for getDashboardItemsByType")
                snapshot.children.forEach { postSnapshot ->
                    val uid = postSnapshot.key // Mendapatkan UID dari snapshot
                    val item = postSnapshot.getValue(DashboardItem::class.java)
                    item?.let {
                        // Memperbarui UID item dengan UID yang benar
                        it.uid = uid ?: ""
                        items.add(it)
                    }
                }
                count++
                if (count == totalCount) {
                    Log.d(
                        "DashboardRepository",
                        "All queries completed for getDashboardItemsByType"
                    )
                    callback(items)
                }
            }

            override fun onCancelled(error: DatabaseError) {
                Log.e("DashboardRepository", "Error in getDashboardItemsByType: ${error.message}")
                // Handle error
            }
        }

        jualQuery.addListenerForSingleValueEvent(mergedListener)
        sewaQuery.addListenerForSingleValueEvent(mergedListener)
    }

    fun getDashboardItemsByLocation(
        kecamatan: String,
        callback: (List<DashboardItem>) -> Unit
    ) {
        val jualQuery = databaseReference.child("Jual")
            .orderByChild("kecamatan")
            .equalTo(kecamatan)

        val sewaQuery = databaseReference.child("Sewa")
            .orderByChild("kecamatan")
            .equalTo(kecamatan)

        val items = mutableListOf<DashboardItem>()

        var count = 0
        val totalCount = 2 // Total jumlah query yang akan dieksekusi

        val mergedListener = object : ValueEventListener {
            override fun onDataChange(snapshot: DataSnapshot) {
                Log.d("DashboardRepository", "onDataChange called for getDashboardItemsByLocation")
                snapshot.children.forEach { postSnapshot ->
                    val uid = postSnapshot.key // Mendapatkan UID dari snapshot
                    val item = postSnapshot.getValue(DashboardItem::class.java)
                    item?.let {
                        // Memperbarui UID item dengan UID yang benar
                        it.uid = uid ?: ""
                        items.add(it)
                    }
                }
                count++
                if (count == totalCount) {
                    Log.d(
                        "DashboardRepository",
                        "All queries completed for getDashboardItemsByLocation"
                    )
                    callback(items)
                }
            }

            override fun onCancelled(error: DatabaseError) {
                Log.e(
                    "DashboardRepository",
                    "Error in getDashboardItemsByLocation: ${error.message}"
                )
                // Handle error
            }
        }

        jualQuery.addListenerForSingleValueEvent(mergedListener)
        sewaQuery.addListenerForSingleValueEvent(mergedListener)
    }


    fun getDashboardItemById(
        itemUid: String,
        callback: (ListItem?) -> Unit
    ) {
        databaseReference.addListenerForSingleValueEvent(object : ValueEventListener {
            override fun onDataChange(dataSnapshot: DataSnapshot) {
                val item = dataSnapshot.children.firstOrNull { it.child("uid").value == itemUid }
                    ?.getValue(ListItem::class.java)
                callback(item)
            }

            override fun onCancelled(databaseError: DatabaseError) {
                Log.e(
                    "DashboardRepository",
                    "Error in getDashboardItemById: ${databaseError.message}"
                )
                callback(null)
            }
        })

    }
}