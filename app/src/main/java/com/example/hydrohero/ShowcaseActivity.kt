package com.example.hydrohero

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.hydrohero.database.DatabaseProvider
import com.example.hydrohero.navigation.NavigationHost
import com.example.hydrohero.ui.theme.LoginViewModel
import com.example.hydrohero.ui.theme.MainViewModel
import com.example.hydrohero.ui.theme.MainViewModelFactory
import com.google.firebase.FirebaseApp

class ShowcaseActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        FirebaseApp.initializeApp(this)
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()

        val dao = DatabaseProvider.provideWaterIntakeDao(this)
        val mainViewModelFactory = MainViewModelFactory(dao, this)
        val mainViewModel = MainViewModel(dao, this)
        val loginViewModel = LoginViewModel(application)

        setContent {
            NavigationHost(loginViewModel, mainViewModel)
        }
    }
}
