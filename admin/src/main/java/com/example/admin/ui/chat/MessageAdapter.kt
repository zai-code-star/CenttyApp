package com.example.admin.ui.chat

import android.content.ClipData
import android.content.ClipboardManager
import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.RelativeLayout
import android.widget.TextView
import android.widget.Toast
import androidx.recyclerview.widget.RecyclerView
import com.example.admin.R
import com.example.admin.data.model.ChatMessage

class MessageAdapter : RecyclerView.Adapter<MessageAdapter.MessageViewHolder>() {
    private var messages: List<ChatMessage> = ArrayList()

    fun submitList(messages: List<ChatMessage>) {
        this.messages = messages
        notifyDataSetChanged()
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): MessageViewHolder {
        val view = LayoutInflater.from(parent.context).inflate(R.layout.item_message, parent, false)
        return MessageViewHolder(view)
    }

    override fun onBindViewHolder(holder: MessageViewHolder, position: Int) {
        val message = messages[position]
        holder.bind(message)
    }

    override fun getItemCount(): Int {
        return messages.size
    }

    inner class MessageViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        private val messageText: TextView = itemView.findViewById(R.id.messageText)

        init {
            // Tambahkan listener klik ke TextView untuk menyalin teks ke clipboard saat pengguna mengetuknya
            messageText.setOnLongClickListener {
                // Salin teks pesan ke clipboard
                val clipboardManager = itemView.context.getSystemService(Context.CLIPBOARD_SERVICE) as ClipboardManager
                val clip = ClipData.newPlainText("Message", messageText.text)
                clipboardManager.setPrimaryClip(clip)

                // Tampilkan pesan toast bahwa teks telah disalin
                Toast.makeText(itemView.context, "Pesan disalin", Toast.LENGTH_SHORT).show()

                // Mengembalikan true menandakan bahwa kejadian sudah ditangani
                true
            }
        }

        fun bind(message: ChatMessage) {
            messageText.text = message.message

            val params = messageText.layoutParams as RelativeLayout.LayoutParams
            val context = itemView.context

            // Menyesuaikan tata letak pesan berdasarkan apakah pesan berasal dari pengguna atau bukan
            if (message.isFromMe) {
                // Pesan dari pengguna, tampilkan di sebelah kanan
                params.addRule(RelativeLayout.ALIGN_PARENT_END)
                params.addRule(RelativeLayout.ALIGN_PARENT_RIGHT)
                params.removeRule(RelativeLayout.ALIGN_PARENT_START)
                params.removeRule(RelativeLayout.ALIGN_PARENT_LEFT)
                messageText.setBackgroundResource(R.drawable.outgoing_message_bg)
            } else {
                // Pesan bukan dari pengguna, tampilkan di sebelah kiri
                params.addRule(RelativeLayout.ALIGN_PARENT_START)
                params.addRule(RelativeLayout.ALIGN_PARENT_LEFT)
                params.removeRule(RelativeLayout.ALIGN_PARENT_END)
                params.removeRule(RelativeLayout.ALIGN_PARENT_RIGHT)
                messageText.setBackgroundResource(R.drawable.incoming_message_bg)
            }
        }
    }
}