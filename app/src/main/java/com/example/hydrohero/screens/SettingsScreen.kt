package com.example.hydrohero.screens

import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.dimensionResource
import androidx.compose.ui.unit.dp
import com.example.hydrohero.R
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.hydrohero.ui.theme.MainViewModel

@Composable
fun SettingsScreen(viewModel: MainViewModel = viewModel()) {
    val uiState by viewModel.uiState.collectAsState()
    var goalInput by remember { mutableStateOf(uiState.dailyGoal.toString()) }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Text(
            text = "Settings",
            style = MaterialTheme.typography.headlineMedium
        )
        Spacer(modifier = Modifier.height(dimensionResource(R.dimen.padding_medium)))
        OutlinedTextField(
            value = goalInput,
            onValueChange = { goalInput = it },
            label = { Text("Daily Water Goal (ml)") }
        )
        Spacer(modifier = Modifier.height(dimensionResource(R.dimen.padding_medium)))
        Button(
            onClick = {
                goalInput.toIntOrNull()?.let { viewModel.setDailyGoal(it) }
            }
        ) {
            Text("Save Goal")
        }
    }
}