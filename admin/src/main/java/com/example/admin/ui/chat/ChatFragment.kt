package com.example.admin.ui.chat

import android.content.Intent
import android.os.Bundle
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ProgressBar
import com.example.admin.R
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView

class ChatFragment : Fragment() {
    private lateinit var viewModel: ChatViewModel
    private lateinit var userAdapter: UserAdapter
    private lateinit var recyclerView: RecyclerView
    private lateinit var progressBar: ProgressBar

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        val view = inflater.inflate(R.layout.fragment_chat, container, false)
        recyclerView = view.findViewById(R.id.recycler_view_chat)
        userAdapter = UserAdapter { user ->
            // Handle user click
            val intent = Intent(activity, ChatDetailActivity::class.java)
            intent.putExtra("userId", user.id)
            startActivity(intent)
        }
        recyclerView.adapter = userAdapter
        recyclerView.layoutManager = LinearLayoutManager(requireContext()) // Tambahkan layout manager di sini

        // Initialize ProgressBar
        progressBar = view.findViewById(R.id.progressBar)

        // Menampilkan ProgressBar saat memuat data
        progressBar.visibility = View.VISIBLE

        return view
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        viewModel = ViewModelProvider(this)[ChatViewModel::class.java]

        viewModel.getMessages { messages ->
            val users = messages.map { it.senderId }.distinct().map { User(it) }
            val reversedUsers = users.reversed() // Membalik urutan data sebelum ditampilkan
            userAdapter.submitList(reversedUsers)

            Log.d("ChatFragment", "Data berhasil diambil dari userdata: $users")
            Log.d("ChatFragment", "Jumlah pengguna yang ditampilkan: ${users.size}")

            // Sembunyikan ProgressBar setelah data dimuat
            progressBar.visibility = View.GONE
        }
    }
}
