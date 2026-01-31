package com.example.admin.ui.chat

import android.content.pm.PackageManager
import android.net.Uri
import android.os.Bundle
import android.util.Log
import android.widget.EditText
import android.widget.ImageButton
import androidx.appcompat.app.AppCompatActivity
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.admin.R
import com.example.admin.data.model.ChatMessage
import com.google.android.gms.location.FusedLocationProviderClient
import com.google.android.gms.location.LocationCallback
import com.google.android.gms.location.LocationRequest
import com.google.android.gms.location.LocationResult
import com.google.android.gms.location.LocationServices

class ChatDetailActivity : AppCompatActivity() {
    private lateinit var viewModel: ChatViewModel
    private lateinit var messageAdapter: MessageAdapter
    private lateinit var recyclerView: RecyclerView
    private lateinit var messageEditText: EditText
    private lateinit var sendButton: ImageButton
    private lateinit var shareLocationButton: ImageButton
    private lateinit var fusedLocationClient: FusedLocationProviderClient

    private val LOCATION_PERMISSION_REQUEST_CODE = 1001
    private var isLocationShared = false

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_chat_detail)

        val userId = intent.getStringExtra("userId") ?: return
        Log.d("ChatDetailActivity", "User ID: $userId")

        viewModel = ViewModelProvider(this)[ChatViewModel::class.java]

        recyclerView = findViewById(R.id.recyclerView)
        recyclerView.layoutManager = LinearLayoutManager(this)
        messageEditText = findViewById(R.id.messageEditText)
        sendButton = findViewById(R.id.sendButton)
        shareLocationButton = findViewById(R.id.locationButton)
        fusedLocationClient = LocationServices.getFusedLocationProviderClient(this)

        messageAdapter = MessageAdapter()
        recyclerView.adapter = messageAdapter

        sendButton.setOnClickListener {
            val message = messageEditText.text.toString().trim()
            if (message.isNotEmpty()) {
                sendMessage(message, userId)
                messageEditText.text.clear()

                viewModel.addMessage(
                    ChatMessage(userId, message, System.currentTimeMillis()),
                    isFromMe = true
                )
            }
        }

        shareLocationButton.setOnClickListener {
            if (ContextCompat.checkSelfPermission(this, android.Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
                ActivityCompat.requestPermissions(
                    this,
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

        viewModel.getMessages { messages ->
            val userMessages = messages.filter { it.senderId == userId }
            val sortedMessages = userMessages.sortedBy { it.timestamp }
            messageAdapter.submitList(sortedMessages)
            recyclerView.scrollToPosition(sortedMessages.size - 1)

            for (message in userMessages) {
                Log.d("ChatDetailActivity", "Message: ${message.message}")
                Log.d("ChatDetailActivity", "Sender ID: ${message.senderId}")
                Log.d("ChatDetailActivity", "Is From Me: ${message.isFromMe}")
            }
        }
    }

    private fun sendMessage(message: String, userId: String) {
        if (ContextCompat.checkSelfPermission(this, android.Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(
                this,
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
            override fun onLocationResult(locationResult: LocationResult?) {
                locationResult ?: return
                for (location in locationResult.locations) {
                    val latitude = location.latitude
                    val longitude = location.longitude

                    val gmmIntentUri = Uri.parse("http://maps.google.com/maps?q=loc:$latitude,$longitude")
                    val locationUri = gmmIntentUri.toString()
                    callback.invoke(locationUri)
                    isLocationShared = true // Set isLocationShared to true when location is shared

                    // Hentikan pembaruan lokasi setelah lokasi ditemukan
                    fusedLocationClient.removeLocationUpdates(this)
                    return
                }
            }
        }

        if (ContextCompat.checkSelfPermission(this, android.Manifest.permission.ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_GRANTED) {
            fusedLocationClient.requestLocationUpdates(
                locationRequest,
                locationCallback,
                null
            )
        } else {
            // Handle case when permission is not granted
            // You can display a message or request permission again
        }
    }

    override fun onRequestPermissionsResult(requestCode: Int, permissions: Array<out String>, grantResults: IntArray) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)
        if (requestCode == LOCATION_PERMISSION_REQUEST_CODE) {
            if (grantResults.isNotEmpty() && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                if (!isLocationShared) { // Tambahkan pengecekan isLocationShared
                    getLocationAndShare { locationUri ->
                        // Handle location share
                    }
                }
            } else {
    // Permission denied, handle accordingly
            }
        }
    }
}
