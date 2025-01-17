package com.example.hydrohero

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import com.example.hydrohero.database.DatabaseProvider
import com.example.hydrohero.navigation.NavigationHost
import com.example.hydrohero.ui.theme.MainViewModelFactory

class ShowcaseActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()

        val dao = DatabaseProvider.provideWaterIntakeDao(this)
        val viewModelFactory = MainViewModelFactory(dao)

        setContent {
            NavigationHost(viewModelFactory)
        }
    }
}
