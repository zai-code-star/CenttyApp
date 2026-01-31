package com.example.admin.data.model

data class TransactionItemSewa(
    val id: String? = null,
    val properti: String = "",
    val namaPenyewa: String? = null,
    val pemilikSewa: String? = null,
    val biayaSewa: Double? = null,
    val tanggalSewa: String? = null,
    val tanggalSelesai: String? = null
) {
    // Konstruktor tanpa argumen diperlukan oleh Firebase
    constructor() : this(null)
}

data class TransactionItemJual(
    val id: String? = null,
    val properti: String = "",
    val harga: Double? = null,
    val pemilik: String = "",
    val tanggal: String? = null // Tanggal transaksi
) {
    // Konstruktor tanpa argumen diperlukan oleh Firebase
    constructor() : this(null)
}
