package com.example.admin.data.model

data class ChatMessage(
    val senderId: String = "",
    val message: String = "",
    val timestamp: Long = 0,
    var isFromMe: Boolean = false, // Menyatakan apakah pesan berasal dari Anda
    val imageUri: String? = null // Menyimpan URL gambar atau referensi gambar
)


