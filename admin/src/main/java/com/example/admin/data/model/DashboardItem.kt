package com.example.admin.data.model

data class DashboardItem(
    val uid: String = "", // UID untuk entri
    val nama: String = "",
    val harga: Double = 0.0, // Mengubah tipe data harga menjadi Double
    val alamat: String = "",
    val kecamatan: String = "", // Menambah properti untuk kecamatan
    val tipeProperti: String = "", // Menambah properti untuk tipe properti
    val spesifikasi: String = "", // Menambah properti untuk spesifikasi
    val uriGambar: List<String>? = null
)
