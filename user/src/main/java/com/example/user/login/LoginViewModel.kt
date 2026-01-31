package com.example.user.login

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseUser
import com.google.firebase.auth.GoogleAuthProvider

class LoginViewModel : ViewModel() {

    private val _authenticationResult = MutableLiveData<Boolean>()
    val authenticationResult: LiveData<Boolean>
        get() = _authenticationResult

    fun authenticateUserWithGoogle(idToken: String) {
        val credential = FirebaseAuth.getInstance().signInWithCredential(GoogleAuthProvider.getCredential(idToken, null))
        credential.addOnCompleteListener { task ->
            _authenticationResult.value = task.isSuccessful
        }
    }

    // Metode untuk mendapatkan pengguna saat ini
    fun getCurrentUser(): FirebaseUser? {
        return FirebaseAuth.getInstance().currentUser
    }
}

