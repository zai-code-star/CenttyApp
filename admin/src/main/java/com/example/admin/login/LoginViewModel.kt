package com.example.admin.login

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.google.firebase.auth.FirebaseAuth

class LoginViewModel : ViewModel() {

    private val _authenticationResult = MutableLiveData<Boolean>()
    val authenticationResult: LiveData<Boolean>
        get() = _authenticationResult

    fun authenticateUser(email: String, password: String) {
        // Authenticate user using Firebase Authentication
        FirebaseAuth.getInstance().signInWithEmailAndPassword(email, password)
            .addOnCompleteListener { task ->
                _authenticationResult.value = task.isSuccessful
            }
    }
}
