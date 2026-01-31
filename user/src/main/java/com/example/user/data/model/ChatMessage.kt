package com.example.user.data.model

data class ChatMessage(
    val message: String = "",
    val senderId: String = "",
    val timestamp: Long = 0,
    val fromUser: Boolean = false // Menandakan apakah pesan berasal dari pengguna (true) atau ke pengguna (false)
)


