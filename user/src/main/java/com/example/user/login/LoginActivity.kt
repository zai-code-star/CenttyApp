package com.example.user.login

import android.content.ContentValues.TAG
import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.widget.Toast
import androidx.activity.result.contract.ActivityResultContracts
import androidx.appcompat.app.AppCompatActivity
import com.example.user.HomeActivity
import com.example.user.R
import com.google.android.gms.auth.api.signin.GoogleSignIn
import com.google.android.gms.auth.api.signin.GoogleSignInAccount
import com.google.android.gms.auth.api.signin.GoogleSignInClient
import com.google.android.gms.auth.api.signin.GoogleSignInOptions
import com.google.android.gms.common.SignInButton
import com.google.android.gms.common.api.ApiException
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.GoogleAuthProvider
import com.google.firebase.database.FirebaseDatabase

class LoginActivity : AppCompatActivity() {

    private lateinit var googleSignInClient: GoogleSignInClient
    private lateinit var firebaseAuth: FirebaseAuth

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_login)

        firebaseAuth = FirebaseAuth.getInstance()

        // Konfigurasi untuk login dengan Google
        val gso = GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
            .requestIdToken(getString(R.string.default_web_client_id))
            .requestEmail()
            .build()

        googleSignInClient = GoogleSignIn.getClient(this, gso)

        val signInButton: SignInButton = findViewById(R.id.signInButton)

        // Set click listener untuk login dengan Google
        signInButton.setOnClickListener {
            Log.d(TAG, "Login button clicked")
            val signInIntent = googleSignInClient.signInIntent
            startForResult.launch(signInIntent)
        }
    }

    override fun onStart() {
        super.onStart()

        // Check if user is already signed in (by Firebase Auth)
        val currentUser = firebaseAuth.currentUser
        if (currentUser != null) {
            // User is already signed in, navigate to HomeActivity
            navigateToHome()
        }
    }

    private fun navigateToHome() {
            val intent = Intent(this, HomeActivity::class.java)
            startActivity(intent)
            finish()
        }


    // Handle hasil login dengan Google
    private val startForResult =
        registerForActivityResult(ActivityResultContracts.StartActivityForResult()) { result ->
            if (result.resultCode == RESULT_OK) {
                Log.d(TAG, "Login activity result OK")
                val task = GoogleSignIn.getSignedInAccountFromIntent(result.data)
                try {
                    val account = task.getResult(ApiException::class.java)
                    firebaseAuthWithGoogle(account)
                } catch (e: ApiException) {
                    // Handle jika ada kesalahan
                    Log.w(TAG, "Google sign in failed", e)
                    Toast.makeText(this, "Google sign in failed", Toast.LENGTH_SHORT).show()
                }
            }
        }

    // Autentikasi dengan Firebase menggunakan akun Google
    private fun firebaseAuthWithGoogle(account: GoogleSignInAccount?) {
        Log.d(TAG, "Firebase authentication with Google")
        val credential = GoogleAuthProvider.getCredential(account?.idToken, null)
        firebaseAuth.signInWithCredential(credential)
            .addOnCompleteListener(this) { task ->
                if (task.isSuccessful) {
                    // Sign in success
                    Log.d(TAG, "Firebase authentication successful")
                    val user = firebaseAuth.currentUser

                    // Simpan data pengguna ke Firebase Realtime Database
                    user?.let {
                        val database = FirebaseDatabase.getInstance().reference
                        val userRef = database.child("users").child(user.uid)
                        val userData = HashMap<String, Any>()
                        userData["email"] = user.email ?: ""
                        userData["name"] = user.displayName ?: ""

                        userRef.setValue(userData)
                            .addOnSuccessListener {
                                // Berhasil menyimpan data pengguna
                                Toast.makeText(
                                    baseContext,
                                    "Data pengguna disimpan",
                                    Toast.LENGTH_SHORT
                                ).show()

                                // Intent ke HomeActivity setelah login sukses
                                val intent = Intent(this, HomeActivity::class.java)
                                startActivity(intent)
                                finish() // Optional, untuk menutup activity login setelah pindah ke HomeActivity
                            }
                            .addOnFailureListener { e ->
                                // Gagal menyimpan data pengguna
                                Log.w(TAG, "Error writing user data", e)
                                Toast.makeText(
                                    baseContext,
                                    "Firebase authentication successful",
                                    Toast.LENGTH_SHORT
                                ).show()
                            }
                    }

                } else {
                    // If sign in fails, display a message to the user.
                    Log.w(TAG, "Firebase authentication failed", task.exception)
                    Toast.makeText(
                        baseContext, "Authentication failed.",
                        Toast.LENGTH_SHORT
                    ).show()
                }
            }
    }
}
