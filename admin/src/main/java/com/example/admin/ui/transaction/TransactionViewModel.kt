package com.example.admin.ui.transaction

import androidx.lifecycle.ViewModel
import com.example.admin.data.model.TransactionItemJual
import com.example.admin.data.model.TransactionItemSewa
import com.example.admin.data.repository.TransactionRepository
import com.google.android.gms.tasks.Task


class TransactionViewModel(private val repository: TransactionRepository) : ViewModel() {

    // Add a secondary constructor without parameters
    constructor() : this(TransactionRepository())

    fun saveTransaction(transaction: Any, branch: String): Task<Void> {
        return when (transaction) {
            is TransactionItemJual -> {
                repository.saveTransaction(transaction, branch)
            }

            is TransactionItemSewa -> {
                repository.saveTransaction(transaction, branch)
            }

            else -> {
                // Handle unexpected transaction type
                throw IllegalArgumentException("Unsupported transaction type")
            }
        }
    }
}
