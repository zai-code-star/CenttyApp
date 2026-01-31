package com.example.admin.ui.chat

import androidx.lifecycle.ViewModel
import com.example.admin.data.model.ChatMessage
import com.example.admin.data.repository.ChatRepository
import com.example.admin.data.repository.TransactionRepository

class ChatViewModel(private val repository: ChatRepository) : ViewModel() {

    // Add a secondary constructor without parameters
    constructor() : this(ChatRepository())

    fun getMessages(callback: (List<ChatMessage>) -> Unit) {
        repository.getMessages(callback)
    }

    // Update the addMessage method to accept isFromMe parameter
    fun addMessage(message: ChatMessage, isFromMe: Boolean) {
        repository.addMessage(message, isFromMe)
    }
}
