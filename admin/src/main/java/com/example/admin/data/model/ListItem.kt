package com.example.admin.data.model

data class ListItem(
    val uid: String = "", // UID untuk entri
    val nama: String = "",
    val harga: Double = 0.0, // Mengubah tipe data harga menjadi Double
    val alamat: String = "",
    val kecamatan: String = "", // Menambah properti untuk kecamatan
    val tipeProperti: String = "", // Menambah properti untuk tipe properti
    val spesifikasi: String = "",
    val tipe_listing: String = "",
    val uriGambar: List<String>? = null,
    var totalView: Int = 0 // Menambah field total view dengan nilai default 0
)
