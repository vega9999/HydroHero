package com.example.hydrohero.ui.theme

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch
import android.content.Context
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.ktx.userProfileChangeRequest
import kotlinx.coroutines.tasks.await

class LoginViewModel(application: Application) : AndroidViewModel(application) {
    private val _username = MutableStateFlow<String?>(null)
    val username: StateFlow<String?> get() = _username

    private val _isLoggedIn = MutableStateFlow(false)
    val isLoggedIn: StateFlow<Boolean> get() = _isLoggedIn

    private val firebaseAuth: FirebaseAuth by lazy {
        FirebaseAuth.getInstance()
    }
    private val sharedPreferences = application.getSharedPreferences("login_prefs", Context.MODE_PRIVATE)

    private val _errorMessage = MutableStateFlow<String?>(null)
    val errorMessage: StateFlow<String?> get() = _errorMessage
    init {
        val savedUsername = sharedPreferences.getString("username", null)
        if (savedUsername != null) {
            _username.value = savedUsername
            _isLoggedIn.value = true
        }
    }

    fun updateUsername(newUsername: String) {
        _username.value = newUsername
        sharedPreferences.edit().putString("username", newUsername).apply()
    }

    fun loginWithEmail(email: String, password: String) {
        viewModelScope.launch {
            try {
                val result = firebaseAuth.signInWithEmailAndPassword(email, password).await()
                val user = result.user
                if (user != null) {
                    val username = user.displayName ?: "Unknown User"
                    _username.value = username
                    _isLoggedIn.value = true
                    saveLoginState(username)
                }
            } catch (e: Exception) {
                println("Login fehlgeschlagen: ${e.message}")
            }
        }
    }

    fun setErrorMessage(message: String) {
        _errorMessage.value = message
    }

    fun registerWithEmail(email: String, username: String, password: String) {
        viewModelScope.launch {
            try {
                val existingMethods = firebaseAuth.fetchSignInMethodsForEmail(email).await()
                if (existingMethods.signInMethods?.isNotEmpty() == true) {
                    _errorMessage.value = "Username or E-Mail already in use"
                    return@launch
                }

                val result = firebaseAuth.createUserWithEmailAndPassword(email, password).await()
                val user = result.user
                if (user != null) {
                    val profileUpdates = userProfileChangeRequest {
                        displayName = username
                    }
                    user.updateProfile(profileUpdates).await()
                    _username.value = username
                    _isLoggedIn.value = true
                }
            } catch (e: Exception) {
                setErrorMessage("An error occurred: ${e.message}")
            }
        }
    }

    fun useWithoutRegistering() {
        loginWithUsername("Guest")
    }

    private fun loginWithUsername(username: String) {
        viewModelScope.launch {
            _username.value = username
            _isLoggedIn.value = true
            saveLoginState(username)
        }
    }

    suspend fun tryLoginWithEmail(email: String, password: String): Boolean {
        return try {
            val result = firebaseAuth.signInWithEmailAndPassword(email, password).await()
            val user = result.user
            if (user != null) {
                val username = user.displayName ?: "Unknown User"
                _username.value = username
                _isLoggedIn.value = true
                saveLoginState(username)
                true
            } else {
                false
            }
        } catch (e: Exception) {
            false
        }
    }

    fun sendPasswordResetEmail(email: String) {
        // TODO: Implement actual password reset logic
        println("Password reset email sent to $email")
    }

    private fun saveLoginState(username: String) {
        sharedPreferences.edit().putString("username", username).apply()
    }

    private fun clearLoginState() {
        sharedPreferences.edit().remove("username").apply()
    }

    fun logout() {
        viewModelScope.launch {
            firebaseAuth.signOut()
            _username.value = null
            _isLoggedIn.value = false
            clearLoginState()
        }
    }
}