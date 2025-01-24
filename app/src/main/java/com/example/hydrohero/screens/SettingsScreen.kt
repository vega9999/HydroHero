package com.example.hydrohero.screens

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.colorResource
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.hydrohero.R
import com.example.hydrohero.database.ActivityLevel
import com.example.hydrohero.database.Gender
import com.example.hydrohero.ui.theme.MainViewModel

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun SettingsScreen(
    modifier: Modifier = Modifier,
    viewModel: MainViewModel = viewModel(),
    onBackPressed: () -> Unit
) {
    val uiState by viewModel.uiState.collectAsState()
    val profile = uiState.userProfile

    var weightInput by remember { mutableStateOf(profile.weightKg.toString()) }
    var ageInput by remember { mutableStateOf(profile.age.toString()) }
    var locationInput by remember { mutableStateOf(profile.location) }

    var activityExpanded by remember { mutableStateOf(false) }
    var genderExpanded by remember { mutableStateOf(false) }

    var selectedActivityLevel by remember {
        mutableStateOf(ActivityLevel.valueOf(profile.activityLevel.uppercase()))
    }
    var selectedGender by remember {
        mutableStateOf(Gender.valueOf(profile.gender.uppercase()))
    }

    Column(
        modifier = modifier
            .fillMaxSize()
            .padding(16.dp)
            .verticalScroll(rememberScrollState()),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        // Suggested Water Intake
        Text(
            text = "Suggested Water Intake: ${uiState.dailyGoal} ml",
            style = MaterialTheme.typography.titleLarge
        )

        Spacer(modifier = Modifier.height(16.dp))

        // Weight Input
        OutlinedTextField(
            value = weightInput,
            onValueChange = { newValue ->
                weightInput = newValue.filter { it.isDigit() || it == '.' }
            },
            label = { Text("Weight (kg)") },
            keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number)
        )

        // Age Input
        OutlinedTextField(
            value = ageInput,
            onValueChange = { newValue ->
                ageInput = newValue.filter { it.isDigit() }
            },
            label = { Text("Age") },
            keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number)
        )

        // Location Input
        OutlinedTextField(
            value = locationInput,
            onValueChange = { locationInput = it },
            label = { Text("Location") }
        )

        // Activity Level Dropdown
        ExposedDropdownMenuBox(
            expanded = activityExpanded,
            onExpandedChange = { activityExpanded = !activityExpanded }
        ) {
            OutlinedTextField(
                value = selectedActivityLevel.name,
                onValueChange = {},
                label = { Text("Activity Level") },
                readOnly = true,
                trailingIcon = {
                    ExposedDropdownMenuDefaults.TrailingIcon(
                        expanded = activityExpanded
                    )
                },
                colors = ExposedDropdownMenuDefaults.outlinedTextFieldColors(),
                modifier = Modifier.menuAnchor()
            )

            ExposedDropdownMenu(
                expanded = activityExpanded,
                onDismissRequest = { activityExpanded = false }
            ) {
                ActivityLevel.values().forEach { level ->
                    DropdownMenuItem(
                        text = { Text(level.name) },
                        onClick = {
                            selectedActivityLevel = level
                            activityExpanded = false
                        }
                    )
                }
            }
        }

        // Gender Dropdown
        ExposedDropdownMenuBox(
            expanded = genderExpanded,
            onExpandedChange = { genderExpanded = !genderExpanded }
        ) {
            OutlinedTextField(
                value = selectedGender.name,
                onValueChange = {},
                label = { Text("Gender") },
                readOnly = true,
                trailingIcon = {
                    ExposedDropdownMenuDefaults.TrailingIcon(
                        expanded = genderExpanded
                    )
                },
                colors = ExposedDropdownMenuDefaults.outlinedTextFieldColors(),
                modifier = Modifier.menuAnchor()
            )

            ExposedDropdownMenu(
                expanded = genderExpanded,
                onDismissRequest = { genderExpanded = false }
            ) {
                Gender.values().forEach { gender ->
                    DropdownMenuItem(
                        text = { Text(gender.name) },
                        onClick = {
                            selectedGender = gender
                            genderExpanded = false
                        }
                    )
                }
            }
        }

        Button(
            onClick = {
                viewModel.updateUserProfile(
                    weightKg = weightInput.toDoubleOrNull() ?: profile.weightKg,
                    age = ageInput.toIntOrNull() ?: profile.age,
                    location = locationInput,
                    activityLevel = selectedActivityLevel.name.lowercase(),
                    gender = selectedGender.name.lowercase(),
                )
            },
            colors = ButtonDefaults.buttonColors(containerColor = colorResource(id = R.color.dark_blue))

        ) {
            Text("Save Profile")
        }
    }
}
