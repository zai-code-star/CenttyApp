package com.example.admin.data.repository

import com.example.admin.data.model.DashboardItem
import com.google.firebase.database.DatabaseReference
import com.google.firebase.database.FirebaseDatabase

class DashboardRepository {
    private val databaseReference: DatabaseReference by lazy {
        FirebaseDatabase.getInstance().getReference("postings")
    }

    // Metode untuk menyimpan postingan jenis "Jual" ke Firebase
    fun simpanPostingJual(posting: DashboardItem) {
        val key = databaseReference.child("Jual").push().key
        key?.let {
            databaseReference.child("Jual").child(it).setValue(posting)
        }
    }

    // Metode untuk menyimpan postingan jenis "Sewa" ke Firebase
    fun simpanPostingSewa(posting: DashboardItem) {
        val key = databaseReference.child("Sewa").push().key
        key?.let {
            databaseReference.child("Sewa").child(it).setValue(posting)
        }
    }
}
