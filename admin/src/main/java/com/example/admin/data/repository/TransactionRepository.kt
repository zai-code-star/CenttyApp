package com.example.admin.data.repository

import com.example.admin.data.model.TransactionItemJual
import com.example.admin.data.model.TransactionItemSewa
import com.google.android.gms.tasks.Task
import com.google.android.gms.tasks.TaskCompletionSource
import com.google.android.gms.tasks.Tasks
import com.google.firebase.database.*

class TransactionRepository {
    private val database: FirebaseDatabase = FirebaseDatabase.getInstance()
    private val transactionRef: DatabaseReference = database.reference.child("transaksi")

    fun saveTransaction(transaction: Any, branch: String): Task<Void> {
        val branchRef = transactionRef.child(branch)
        val transactionId = branchRef.push().key

        return if (transactionId != null) {
            val taskCompletionSource = TaskCompletionSource<Void>()
            branchRef.child(transactionId).setValue(transaction)
                .addOnCompleteListener { task ->
                    if (task.isSuccessful) {
                        taskCompletionSource.setResult(null)
                    } else {
                        taskCompletionSource.setException(task.exception!!)
                    }
                }
            taskCompletionSource.task
        } else {
            Tasks.forException<Void>(Exception("Failed to generate transaction ID"))
        }
    }

    fun getTransactions(branch: String, callback: (List<Any>, Double) -> Unit) {
        val branchRef = transactionRef.child(branch)
        val transactionList = mutableListOf<Any>()

        branchRef.addValueEventListener(object : ValueEventListener {
            override fun onDataChange(snapshot: DataSnapshot) {
                var totalHarga = 0.0
                for (data in snapshot.children) {
                    when (branch) {
                        "jual" -> {
                            val transaction = data.getValue(TransactionItemJual::class.java)
                            transaction?.let {
                                transactionList.add(it)
                                totalHarga += it.harga ?: 0.0
                            }
                        }

                        "sewa" -> {
                            val transaction = data.getValue(TransactionItemSewa::class.java)
                            transaction?.let {
                                transactionList.add(it)
                                totalHarga += it.biayaSewa ?: 0.0
                            }
                        }

                        else -> {
                            // Handle unexpected branch value
                        }
                    }
                }
                callback(transactionList, totalHarga)
            }

            override fun onCancelled(error: DatabaseError) {
                // Handle database error
            }
        })
    }
}