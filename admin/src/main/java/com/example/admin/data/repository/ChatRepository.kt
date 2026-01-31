package com.example.admin.data.repository

import com.example.admin.data.model.ChatMessage
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener

class ChatRepository {
    private val database = FirebaseDatabase.getInstance()
    private val chatRef = database.getReference("chats")

    fun addMessage(message: ChatMessage, isFromMe: Boolean) {
        // Set the isFromMe property before saving the message to the database
        message.isFromMe = isFromMe
        val messageId = chatRef.push().key
        messageId?.let {
            chatRef.child(it).setValue(message)
        }
    }


    fun getMessages(callback: (List<ChatMessage>) -> Unit) {
        chatRef.addValueEventListener(object : ValueEventListener {
            override fun onDataChange(snapshot: DataSnapshot) {
                val messages = mutableListOf<ChatMessage>()
                for (messageSnapshot in snapshot.children) {
                    val message = messageSnapshot.getValue(ChatMessage::class.java)
                    message?.let { messages.add(it) }
                }
                callback(messages)
            }

            override fun onCancelled(error: DatabaseError) {
                // Handle error
            }
        })
    }
}