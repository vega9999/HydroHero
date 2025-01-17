package com.example.hydrohero.screens

import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.example.hydrohero.ui.theme.MainViewModel

@Composable
fun MainScreen(
    viewModel: MainViewModel
) {
    val uiState by viewModel.uiState.collectAsState()

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Text(
            text = "HydroHero",
            style = MaterialTheme.typography.headlineMedium
        )
        Spacer(modifier = Modifier.height(16.dp))
        ProgressIndicator(progress = uiState.progress)
        Spacer(modifier = Modifier.height(16.dp))
        Text(
            text = "Current Intake: ${uiState.currentIntake}ml / ${uiState.dailyGoal}ml",
            style = MaterialTheme.typography.bodyLarge
        )
        Spacer(modifier = Modifier.height(16.dp))
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceEvenly
        ) {
            Button(onClick = { viewModel.addWater(200) }) {
                Text("Add 200ml")
            }
            Button(onClick = { viewModel.addWater(500) }) {
                Text("Add 500ml")
            }
        }
        Spacer(modifier = Modifier.height(16.dp))
        Button(onClick = { viewModel.logDailyIntake() }) {
            Text("Log Daily Intake")
        }
        Spacer(modifier = Modifier.height(16.dp))
        Text(
            text = viewModel.getPersonalizedRecommendation(),
            style = MaterialTheme.typography.bodyMedium
        )
        Spacer(modifier = Modifier.height(16.dp))
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceEvenly
        ) {
        }
    }
}

@Composable
fun ProgressIndicator(progress: Float) {
    CircularProgressIndicator(
        progress = progress / 100,
        modifier = Modifier.size(200.dp),
        strokeWidth = 8.dp
    )
}