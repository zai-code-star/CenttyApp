package com.example.admin.ui.dashboard

import androidx.lifecycle.ViewModel
import com.example.admin.data.model.DashboardItem
import com.example.admin.data.repository.DashboardRepository

class DashboardViewModel(private val repository: DashboardRepository) : ViewModel() {

    fun simpanPosting(posting: DashboardItem, jenisPosting: String) {
        // Memeriksa jenis posting dan menambahkannya ke Firebase sesuai jenisnya
        when (jenisPosting) {
            "Jual" -> {
                // Simpan ke Firebase untuk postingan yang dijual
                repository.simpanPostingJual(posting)
            }
            "Sewa" -> {
                // Simpan ke Firebase untuk postingan yang disewakan
                repository.simpanPostingSewa(posting)
            }
            else -> {
                // Handling jika jenis posting tidak dikenali
            }
        }
    }
}
