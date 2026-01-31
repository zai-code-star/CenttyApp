package com.example.admin.login

import android.content.Intent
import android.os.Bundle
import android.widget.Button
import android.widget.EditText
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.ViewModelProvider
import com.example.admin.HomeActivity
import com.example.admin.R


class LoginActivity : AppCompatActivity() {

    private lateinit var viewModel: LoginViewModel
    private lateinit var buttonLogin: Button
    private lateinit var editTextPassword: EditText
    private lateinit var editTextEmail: EditText


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_login)

        val editTextPassword: EditText = findViewById(R.id.editTextPassword)
        val editTextEmail: EditText = findViewById(R.id.editTextEmail)
        val buttonLogin: Button = findViewById(R.id.buttonLogin)

        viewModel = ViewModelProvider(this).get(LoginViewModel::class.java)

        // Set click listener for login button
        buttonLogin.setOnClickListener {
            val email = editTextEmail.text.toString().trim()
            val password = editTextPassword.text.toString().trim()

            // Validate email and password
            if (email.isEmpty() || password.isEmpty()) {
                Toast.makeText(this, "Please enter email and password", Toast.LENGTH_SHORT).show()
            } else {
                // Call ViewModel method to authenticate user
                viewModel.authenticateUser(email, password)
            }
        }

        // Observe authentication result
        viewModel.authenticationResult.observe(this, { result ->
            if (result) {
                // Authentication success, navigate to next activity
                Toast.makeText(this, "Berhasil Masuk", Toast.LENGTH_SHORT).show()
                val intent = Intent(this, HomeActivity::class.java)
                startActivity(intent)
                finish() // Optional: finish current activity if needed
            } else {
                // Authentication failed, show error message or handle accordingly
                Toast.makeText(this, "Email atau Password salah", Toast.LENGTH_SHORT).show()
            }
        })
    }
}
