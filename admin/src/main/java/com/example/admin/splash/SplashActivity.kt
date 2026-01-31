package com.example.admin.splash

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import com.example.admin.HomeActivity
import com.example.admin.R
import com.example.admin.login.LoginActivity
import com.google.firebase.auth.FirebaseAuth

class SplashActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_splash)

        // Implement splash screen logic here (e.g., delay and navigate to login)
        Handler(Looper.getMainLooper()).postDelayed({
            // Cek apakah pengguna sudah login sebelumnya
            val currentUser = FirebaseAuth.getInstance().currentUser
            if (currentUser != null) {
                // Jika sudah login, arahkan ke HomeActivity
                navigateToHome()
            } else {
                // Jika belum login, arahkan ke LoginActivity
                navigateToLogin()
            }
        }, 2000) // 2 seconds delay
    }

    private fun navigateToLogin() {
        startActivity(Intent(this, LoginActivity::class.java))
        finish()
    }

    private fun navigateToHome() {
        val intent = Intent(this, HomeActivity::class.java)
         startActivity(intent)
         finish()
    }
}
