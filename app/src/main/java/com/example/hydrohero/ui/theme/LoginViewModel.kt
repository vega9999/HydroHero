package com.example.hydrohero.ui.theme

import android.app.Application
import android.content.Context
import androidx.lifecycle.AndroidViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update

class LoginViewModel(application: Application) : AndroidViewModel(application) {

    private val sharedPreferences = application.getSharedPreferences(PREF, Context.MODE_PRIVATE)

    // Define StateFlow for UI states
    private val _uiStateFlow = MutableStateFlow(
        MainUIState(
            userName = "${sharedPreferences.getString(USERNAME_KEY, "")}",
            password = "",
            isLoggedIn = sharedPreferences.getBoolean(LOGGED_IN_KEY, false),
            isError = false
        )
    )
    val uiStateFlow = _uiStateFlow.asStateFlow()

    // Data class to define the UI State
    data class MainUIState(
        val userName: String = "",
        val password: String = "",
        val isLoggedIn: Boolean = false,
        val isError: Boolean = false
    )

    // Method to validate login credentials
    fun validateUser() {
        if (_uiStateFlow.value.userName == USERNAME && _uiStateFlow.value.password == PASSWORD) {
            // Update UI state and persist login status
            _uiStateFlow.update {
                it.copy(
                    isError = false,
                    isLoggedIn = true
                )
            }
            // Save login state and username in SharedPreferences
            with(sharedPreferences.edit()) {
                putBoolean(LOGGED_IN_KEY, true)
                putString(USERNAME_KEY, _uiStateFlow.value.userName)
                apply()
            }

        } else {
            // Update UI state to show error
            _uiStateFlow.update { it.copy(isError = true, isLoggedIn = false) }
        }
    }

    // Function to update username in UI state
    fun updateCurrentUsername(currentUsername: String) {
        _uiStateFlow.update { it.copy(userName = currentUsername) }
    }

    // Function to update password in UI state
    fun updateCurrentPassword(currentPassword: String) {
        _uiStateFlow.update { it.copy(password = currentPassword) }
    }

    // Function to clear error state
    fun resetError() {
        _uiStateFlow.update { it.copy(isError = false) }
    }

    // Logout function to clear login status and SharedPreferences
    fun logout() {
        _uiStateFlow.update { it.copy(isLoggedIn = false) }
        with(sharedPreferences.edit()) {
            putBoolean(LoginViewModel.LOGGED_IN_KEY, false)
            putString(LoginViewModel.USERNAME_KEY, _uiStateFlow.value.userName)
            apply()
        }
    }

    companion object {
        private const val USERNAME = "admin"
        private const val PASSWORD = "password123"
        private const val PREF = "LoginStorage"
        private const val LOGGED_IN_KEY = "isLoggedIn"
        private const val USERNAME_KEY = "username"
    }
}