package com.example.user.ui.chat.profileagent

import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.widget.Button
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.user.R
import com.example.user.ui.chat.profileagent.testimoni.Testimony
import com.example.user.ui.chat.profileagent.testimoni.TestimonyAdapter

class AgentProfileActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_agent_profile)

        // Mendapatkan referensi ke elemen UI
        val contactInfoTextView: TextView = findViewById(R.id.contact_info)
        val contactButton: Button = findViewById(R.id.contact_button)

        // Mendapatkan nomor kontak dari TextView
        val contactInfo = contactInfoTextView.text.toString()

        // Memberikan onClickListener pada tombol "Hubungi"
        contactButton.setOnClickListener {
            // Membuat Intent untuk melakukan panggilan telepon
            val dialIntent = Intent(Intent.ACTION_DIAL)
            dialIntent.data = Uri.parse("tel:$contactInfo")

            // Memulai aktivitas panggilan telepon
            startActivity(dialIntent)
        }

        // Contoh data testimoni
        val testimonies = listOf(
            Testimony("Testimoni 1", "Lorem ipsum dolor sit amet.", "url_gambar_1"),
            Testimony("Testimoni 2", "Consectetur adipiscing elit.", "url_gambar_2"),
            Testimony("Testimoni 3", "Sed do eiusmod tempor incididunt.", "url_gambar_3")
        )

        // Inisialisasi RecyclerView dan Adapter
        val recyclerView: RecyclerView = findViewById(R.id.recyclerViewTestimony)
        val adapter = TestimonyAdapter(testimonies)
        recyclerView.adapter = adapter
        recyclerView.layoutManager = LinearLayoutManager(this, LinearLayoutManager.HORIZONTAL, false)

    }
}
