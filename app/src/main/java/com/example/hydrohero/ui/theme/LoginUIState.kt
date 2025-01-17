package com.example.hydrohero.ui.theme

data class LoginUIState(
    val userName: String,
    val password: String,
    val isError: Boolean,
    val isLoggedIn: Boolean,
)