package com.example.hydrohero

import android.content.Context
import android.content.Intent
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.Scaffold
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import androidx.core.view.WindowCompat
import androidx.navigation.compose.rememberNavController
import com.example.hydrohero.database.DatabaseProvider
import com.example.hydrohero.database.WaterIntakeDao
import com.example.hydrohero.navigation.NavigationHost
import com.example.hydrohero.screens.LoginScreen
import com.example.hydrohero.ui.theme.HydroHeroTheme
import com.example.hydrohero.ui.theme.LoginViewModel
import com.example.hydrohero.ui.theme.MainViewModel

class LoginActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        WindowCompat.setDecorFitsSystemWindows(window, false)

        val loginViewModel = LoginViewModel(application)
        val waterIntakeDao = DatabaseProvider.provideWaterIntakeDao(applicationContext)
        val mainViewModel = MainViewModel(waterIntakeDao, applicationContext)

        setContent {
            HydroHeroTheme {
                NavigationHost(
                    loginViewModel = loginViewModel,
                    mainViewModel = mainViewModel
                )
            }
        }
    }
}
