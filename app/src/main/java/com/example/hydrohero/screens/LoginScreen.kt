package com.example.hydrohero.screens

import android.annotation.SuppressLint
import android.content.res.Configuration
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Face
import androidx.compose.material.icons.filled.Lock
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TextFieldDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.colorResource
import androidx.compose.ui.res.dimensionResource
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.text.input.VisualTransformation
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.hydrohero.ui.theme.HydroHeroTheme
import com.example.hydrohero.R
import com.example.hydrohero.ui.theme.LoginViewModel

class Login : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            HydroHeroTheme {
                Scaffold(modifier = Modifier.fillMaxSize()) { innerPadding ->
                    Surface(
                        color = MaterialTheme.colorScheme.background
                    ) {
                        LoginScreen(innerPadding) {}
                    }
                }
            }
        }
    }
}

@SuppressLint("ResourceAsColor")
@Composable
fun LoginScreen(
    innerPadding: PaddingValues,
    loginViewModel: LoginViewModel = viewModel(),
    navigate: () -> Unit
) {
    val uiState by loginViewModel.uiStateFlow.collectAsState()
    val context = LocalContext.current
    var passwordVisible by remember { mutableStateOf(false) }
    var isDataSubmitted by remember { mutableStateOf(false) }

    if (uiState.isLoggedIn) {
        LaunchedEffect(Unit) {
            navigate()
        }
    }

    // Display login form
    Box(
        modifier = Modifier
            .fillMaxSize()
            .padding(innerPadding)
    ) {
        // Background image covering the full screen
        Image(
            painter = painterResource(id = R.drawable.gradient),
            contentDescription = "Background Image",
            contentScale = ContentScale.Crop,
            modifier = Modifier.matchParentSize()
        )
        // Column to arrange the login components vertically
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(dimensionResource(R.dimen.padding_medium)),
            verticalArrangement = Arrangement.Center,
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            // Card component that serves as a container for the login form
            Card(
                shape = RoundedCornerShape(16.dp),
                colors = CardDefaults.cardColors(containerColor = colorResource(R.color.light_blue).copy(alpha = 0.4F)),
                modifier = Modifier.padding(16.dp)
            ) {
                // Column inside the Card to arrange form fields and buttons
                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(dimensionResource(R.dimen.padding_medium)),
                    verticalArrangement = Arrangement.Center,
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    Text(
                        text = stringResource(R.string.login_tag),
                        fontSize = 30.sp,
                        fontWeight = FontWeight.Bold,
                        color = colorResource(R.color.dark_blue)
                    )

                    Spacer(modifier = Modifier.height(dimensionResource(R.dimen.padding_medium)))

                    // Username input field with an icon
                    OutlinedTextField(
                        value = uiState.userName,
                        onValueChange = { loginViewModel.updateCurrentUsername(it) },
                        label = { Text(text = stringResource(R.string.username_tag)) },
                        colors = TextFieldDefaults.colors(
                            focusedLabelColor = colorResource(R.color.dark_blue),
                            unfocusedLabelColor = colorResource(R.color.dark_blue),
                            focusedIndicatorColor = colorResource(R.color.dark_blue),
                            unfocusedIndicatorColor = colorResource(R.color.dark_blue)
                        ),
                        leadingIcon = {
                            Icon(
                                imageVector = Icons.Filled.Face,
                                contentDescription = "Username Icon",
                                modifier = Modifier.size(20.dp)
                            )
                        },
                        modifier = Modifier.size(width = 300.dp, height = 60.dp)
                    )

                    Spacer(modifier = Modifier.height(dimensionResource(R.dimen.padding_medium)))

                    // Password input field with visibility toggle
                    OutlinedTextField(
                        value = uiState.password,
                        onValueChange = { loginViewModel.updateCurrentPassword(it) },
                        label = { Text(text = stringResource(R.string.password_tag)) },
                        colors = TextFieldDefaults.colors().copy(
                            focusedLabelColor = colorResource(R.color.dark_blue),
                            unfocusedLabelColor = colorResource(R.color.dark_blue),
                            focusedIndicatorColor = colorResource(R.color.dark_blue),
                            unfocusedIndicatorColor = colorResource(R.color.dark_blue)
                        ),
                        leadingIcon = {
                            Icon(
                                imageVector = Icons.Filled.Lock,
                                contentDescription = "Password Icon",
                                modifier = Modifier.size(20.dp)
                            )
                        },
                        trailingIcon = {
                            // Toggles the visibility of the password
                            val visibilityIcon = if (passwordVisible)
                                painterResource(id = R.drawable.baseline_visibility_24)
                            else
                                painterResource(id = R.drawable.baseline_visibility_off_24)

                            IconButton(onClick = { passwordVisible = !passwordVisible }) {
                                Icon(
                                    painter = visibilityIcon,
                                    contentDescription = if (passwordVisible) "Hide password" else "Show password"
                                )
                            }
                        },
                        // Changes the password field between plain text and obscured text
                        visualTransformation = if (passwordVisible) VisualTransformation.None else PasswordVisualTransformation(),
                        modifier = Modifier.size(width = 300.dp, height = 60.dp)
                    )

                    Spacer(modifier = Modifier.height(dimensionResource(R.dimen.padding_medium)))

                    // Login button that performs authentication
                    Button(
                        onClick = {
                            loginViewModel.validateUser()
                            isDataSubmitted = true
                        },
                        modifier = Modifier.size(width = 120.dp, height = 40.dp),
                        colors = ButtonDefaults.buttonColors(containerColor = colorResource(R.color.dark_blue))

                    ) {
                        // Text displayed on the login button
                        Text(
                            text = stringResource(R.string.login_button),
                            fontSize = 16.sp,
                        )
                    }
                    if (isDataSubmitted) {
                        Spacer(modifier = Modifier.height(8.dp))
                        Text(
                            text = if (uiState.isLoggedIn) "Login erfolgreich!" else "Falscher Benutzername oder Passwort",
                            color = if (uiState.isLoggedIn) Color(0xFF2E7D32) else Color(0xFFD32F2F)
                        )
                    }
                }
            }
        }
    }
}

@Preview(showBackground = true, showSystemUi = true, uiMode = Configuration.UI_MODE_NIGHT_NO or Configuration.UI_MODE_TYPE_NORMAL)
@Composable
fun LoginScreenPreview() {
    HydroHeroTheme {
        Scaffold(modifier = Modifier.fillMaxSize()) { innerPadding ->
            Surface(
                color = MaterialTheme.colorScheme.background
            ) {
                LoginScreen(innerPadding) {}
            }
        }
    }
}