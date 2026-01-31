package com.example.user.ui.chat

import android.content.Intent
import android.content.pm.PackageManager
import android.net.Uri
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.EditText
import android.widget.ImageButton
import android.widget.ImageView
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.user.R
import com.example.user.data.model.ChatMessage
import com.example.user.ui.chat.profileagent.AgentProfileActivity
import com.google.android.gms.location.FusedLocationProviderClient
import com.google.android.gms.location.LocationCallback
import com.google.android.gms.location.LocationRequest
import com.google.android.gms.location.LocationResult
import com.google.android.gms.location.LocationServices
import com.google.firebase.auth.FirebaseAuth

class ChatFragment : Fragment() {
    private lateinit var viewModel: ChatViewModel
    private lateinit var messageAdapter: MessageAdapter
    private lateinit var recyclerView: RecyclerView
    private lateinit var messageEditText: EditText
    private lateinit var sendButton: ImageButton
    private lateinit var shareLocationButton: ImageButton
    private lateinit var fusedLocationClient: FusedLocationProviderClient
    private lateinit var firebaseAuth: FirebaseAuth
    private lateinit var profileButton: ImageView

    private val LOCATION_PERMISSION_REQUEST_CODE = 1001
    private var isLocationShared = false

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val view = inflater.inflate(R.layout.fragment_chat, container, false)
        firebaseAuth = FirebaseAuth.getInstance()

        viewModel = ViewModelProvider(this).get(ChatViewModel::class.java)

        recyclerView = view.findViewById(R.id.recyclerView)
        recyclerView.layoutManager = LinearLayoutManager(requireContext())
        messageEditText = view.findViewById(R.id.messageEditText)
        sendButton = view.findViewById(R.id.sendButton)
        shareLocationButton = view.findViewById(R.id.locationButton)
        profileButton = view.findViewById(R.id.imageViewProfile)
        fusedLocationClient = LocationServices.getFusedLocationProviderClient(requireActivity())

        messageAdapter = MessageAdapter()
        recyclerView.adapter = messageAdapter

        profileButton.setOnClickListener {
            // Ketika tombol profile diklik, buka activity profil agent
            startActivity(Intent(requireContext(), AgentProfileActivity::class.java))
        }
        sendButton.setOnClickListener {
            val message = messageEditText.text.toString().trim()
            if (message.isNotEmpty()) {
                // Mendapatkan userId dari pengguna yang sudah berhasil masuk
                val userId = firebaseAuth.currentUser?.uid

                // Membuat objek ChatMessage dengan isFromUser yang sesuai
                val chatMessage =
                    ChatMessage(message, userId ?: "", System.currentTimeMillis(), true)

                // Menambahkan pesan ke ViewModel
                viewModel.addMessage(chatMessage)

                // Clear text field after sending message
                messageEditText.text.clear()
            }
        }


        shareLocationButton.setOnClickListener {
            if (ContextCompat.checkSelfPermission(
                    requireContext(),
                    android.Manifest.permission.ACCESS_FINE_LOCATION
                ) != PackageManager.PERMISSION_GRANTED
            ) {
                ActivityCompat.requestPermissions(
                    requireActivity(),
                    arrayOf(android.Manifest.permission.ACCESS_FINE_LOCATION),
                    LOCATION_PERMISSION_REQUEST_CODE
                )
            } else {
                if (!isLocationShared) {
                    getLocationAndShare { locationUri ->
                        val messageWithLocation = "$locationUri"
                        messageEditText.append("\n$messageWithLocation")
                        isLocationShared = true
                    }
                }
            }
        }

        // Dapatkan ID pengguna saat ini dari Firebase Authentication
        val userId = firebaseAuth.currentUser?.uid
        // Gunakan ID pengguna saat memanggil getMessages
        userId?.let {
            viewModel.getMessages { messages ->
                val userMessages = messages.filter { it.senderId == userId }
                val sortedMessages =
                    userMessages.sortedBy { it.timestamp } // Sort messages by timestamp
                messageAdapter.submitList(sortedMessages)
                recyclerView.scrollToPosition(sortedMessages.size - 1) // Scroll to the last position

                for (message in messages) {
                    Log.d("ChatFragment", "Message: ${message.message}")
                }
            }
        }
        return view
    }

    private fun sendMessage(message: String, userId: String) {
        if (ContextCompat.checkSelfPermission(
                requireContext(),
                android.Manifest.permission.ACCESS_FINE_LOCATION
            ) != PackageManager.PERMISSION_GRANTED
        ) {
            ActivityCompat.requestPermissions(
                requireActivity(),
                arrayOf(android.Manifest.permission.ACCESS_FINE_LOCATION),
                LOCATION_PERMISSION_REQUEST_CODE
            )
        } else {
            val messageWithLocation = "$message\n"
            messageEditText.setText(messageWithLocation)
        }
    }

    private fun getLocationAndShare(callback: (String) -> Unit) {
        val locationRequest = LocationRequest.create().apply {
            priority = LocationRequest.PRIORITY_HIGH_ACCURACY
            interval = 10000
            fastestInterval = 5000
        }

        val locationCallback = object : LocationCallback() {
            override fun onLocationResult(locationResult: LocationResult) {
                locationResult ?: return
                for (location in locationResult.locations) {
                    val latitude = location.latitude
                    val longitude = location.longitude

                    // Create Google Maps link
                    val gmmIntentUri =
                        Uri.parse("http://maps.google.com/maps?q=loc:$latitude,$longitude")
                    val locationUri = gmmIntentUri.toString()
                    callback.invoke(locationUri) // Invoke the callback with the location URI
                    isLocationShared = true // Set isLocationShared to true when location is shared

                    // Hentikan pembaruan lokasi setelah lokasi ditemukan
                    fusedLocationClient.removeLocationUpdates(this)
                    return // Exit after getting the location
                }
            }
        }

        // Check permission before requesting location updates
        if (ContextCompat.checkSelfPermission(
                requireContext(),
                android.Manifest.permission.ACCESS_FINE_LOCATION
            ) == PackageManager.PERMISSION_GRANTED
        ) {
            fusedLocationClient.requestLocationUpdates(
                locationRequest,
                locationCallback,
                null
            )
        } else {
            // Handle case when permission is not granted
            // You can display a message or request permission again
            // For simplicity, let's just log an error message
            Log.e("ChatFragment", "Location permission not granted")
        }
    }

    override fun onRequestPermissionsResult(
        requestCode: Int,
        permissions: Array<out String>,
        grantResults: IntArray
    ) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)
        if (requestCode == LOCATION_PERMISSION_REQUEST_CODE) {
            if (grantResults.isNotEmpty() && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                if (!isLocationShared) { // Tambahkan pengecekan isLocationShared
                    getLocationAndShare { locationUri ->
                        // Handle location share
                    }
                } else {
                    // Permission denied, handle accordingly
                }
            }
        }
    }
}
