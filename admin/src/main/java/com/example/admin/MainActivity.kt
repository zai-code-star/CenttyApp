package com.example.admin

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import com.example.admin.splash.SplashActivity

class MainActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        // Redirect to SplashActivity
        startActivity(Intent(this, SplashActivity::class.java))
        // Finish MainActivity to prevent going back when pressing back button
        finish()
    }
}
