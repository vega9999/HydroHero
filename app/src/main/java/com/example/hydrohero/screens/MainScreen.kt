package com.example.hydrohero.screens

import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.colorResource
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavController
import com.example.hydrohero.ui.theme.MainViewModel
import com.example.hydrohero.R

@Composable
fun MainScreen(
    modifier: Modifier = Modifier,
    navController: NavController,
    viewModel: MainViewModel
) {
    val uiState by viewModel.uiState.collectAsState()

    Column(
        modifier = modifier
            .fillMaxSize()
            .padding(16.dp),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Text(
            text = "HydroHero",
            style = MaterialTheme.typography.headlineMedium
        )
        Spacer(modifier = Modifier.height(16.dp))

        CircularProgressIndicator(
            progress = uiState.progress / 100f,
            modifier = Modifier.size(200.dp),
            strokeWidth = 8.dp,
            color = colorResource(R.color.dark_blue)
        )

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
            Button(
                onClick = { viewModel.addWater(200) },
                colors = ButtonDefaults.buttonColors(containerColor = colorResource(R.color.dark_blue)),
            ) {
                Text(
                    text = "Add 200ml",
                )
            }
            Button(
                onClick = { viewModel.addWater(500) },
                colors = ButtonDefaults.buttonColors(containerColor = colorResource(R.color.dark_blue)),
            ) {
                Text(
                    text = "Add 500ml",
                )
            }
        }

        Spacer(modifier = Modifier.height(16.dp))

        Button(
            onClick = { viewModel.logDailyIntake() },
            colors = ButtonDefaults.buttonColors(containerColor = colorResource(R.color.dark_blue)),
        ) {
            Text(
                text = "Log Daily Intake",
            )
        }

        Spacer(modifier = Modifier.height(16.dp))

        Text(
            text = viewModel.getPersonalizedRecommendation(),
            style = MaterialTheme.typography.bodyMedium
        )
    }
}