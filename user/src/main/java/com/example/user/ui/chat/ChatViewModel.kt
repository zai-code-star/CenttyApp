package com.example.user.ui.chat

import androidx.lifecycle.ViewModel
import com.example.user.data.model.ChatMessage
import com.example.user.data.repository.ChatRepository

class ChatViewModel(private val repository: ChatRepository) : ViewModel() {

    // Add a secondary constructor without parameters
    constructor() : this(ChatRepository())

    fun getMessages(callback: (List<ChatMessage>) -> Unit) {
        repository.getMessages(callback)
    }

    // Update the addMessage method to accept ChatMessage object
    fun addMessage(message: ChatMessage) {
        repository.addMessage(message)
    }
}
