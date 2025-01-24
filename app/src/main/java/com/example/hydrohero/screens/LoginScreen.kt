package com.example.hydrohero.screens

import androidx.compose.runtime.*
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.text.ClickableText
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.ArrowForward
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Shadow
import androidx.compose.ui.res.colorResource
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.*
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.text.input.VisualTransformation
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.viewModelScope
import androidx.navigation.NavController
import com.example.hydrohero.R
import com.example.hydrohero.ui.theme.LoginViewModel
import com.google.firebase.auth.FirebaseAuth
import kotlinx.coroutines.launch

@Composable
fun LoginScreen(viewModel: LoginViewModel, navController: NavController) {
    val isLoggedIn by viewModel.isLoggedIn.collectAsState()

    if (isLoggedIn) {
        LaunchedEffect(Unit) {
            navController.navigate("Homepage") {
                popUpTo("login") { inclusive = true }
            }
        }
    }

    val annotatedText = buildAnnotatedString {
        append("Use HydroHero without registering: ")
        val startIndex = length
        append("Here")
        addStyle(
            style = SpanStyle(
                color = colorResource(R.color.dark_blue)
                ),
            start = startIndex,
            end = length
        )
        addStringAnnotation(
            tag = "THIS_WAY",
            annotation = "clickable",
            start = startIndex,
            end = length
        )
    }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(colorResource(id = R.color.white))
            .padding(horizontal = 32.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Top
    ) {
        Spacer(modifier = Modifier.height(120.dp))

        Image(
            painter = painterResource(id = R.drawable.ic_logo),
            contentDescription = "Logo",
            modifier = Modifier.size(250.dp)
        )

        Spacer(modifier = Modifier.height(16.dp))

        Text(
            text = "Sign in or register to take full advantage of HydroHero.",
            fontSize = 16.sp,
            textAlign = TextAlign.Center,
            modifier = Modifier.padding(horizontal = 16.dp)
        )

        Spacer(modifier = Modifier.height(32.dp))

        Button(
            onClick = { navController.navigate("email_login") },
            colors = ButtonDefaults.buttonColors(containerColor = colorResource(R.color.dark_blue)),
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 32.dp)
        ) {
            Text(text = "Login with email", fontSize = 16.sp, color = Color.White)
            Spacer(modifier = Modifier.width(8.dp))
            Icon(Icons.Default.ArrowForward, contentDescription = "Arrow", tint = Color.White)
        }

        Spacer(modifier = Modifier.height(16.dp))

        Button(
            onClick = { navController.navigate("email_registration") },
            colors = ButtonDefaults.buttonColors(containerColor = colorResource(R.color.dark_blue)),
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 32.dp)
        ) {
            Text(text = "Register with email", fontSize = 16.sp, color = Color.White)
            Spacer(modifier = Modifier.width(8.dp))
            Icon(Icons.Default.ArrowForward, contentDescription = "Arrow", tint = Color.White)
        }

        Spacer(modifier = Modifier.height(24.dp))

        ClickableText(
            text = annotatedText,
            onClick = { offset ->
                annotatedText.getStringAnnotations(
                    tag = "THIS_WAY",
                    start = offset,
                    end = offset
                ).firstOrNull()?.let {
                    viewModel.useWithoutRegistering()
                    navController.navigate("Homepage")
                }
            },
            style = TextStyle(fontSize = 16.sp, color = Color.Black)
        )
    }
}

@Composable
fun EmailLoginScreen(viewModel: LoginViewModel, navController: NavController) {
    var email by remember { mutableStateOf("") }
    var password by remember { mutableStateOf("") }
    var passwordVisible by remember { mutableStateOf(false) }
    var isError by remember { mutableStateOf(false) }

    Box(
        modifier = Modifier.fillMaxSize()
    ) {
        IconButton(
            onClick = { navController.popBackStack() },
            modifier = Modifier
                .padding(16.dp)
                .align(Alignment.TopStart)
        ) {
            Icon(
                imageVector = Icons.AutoMirrored.Filled.ArrowBack,
                contentDescription = "Back"
            )
        }

        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(16.dp)
                .padding(top = 64.dp),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Center
        ) {
            Text(
                text = "Login",
                fontSize = 24.sp,
                fontWeight = FontWeight.Bold,
                modifier = Modifier.padding(bottom = 32.dp)
            )

            OutlinedTextField(
                value = email,
                onValueChange = { email = it },
                label = { Text("Email") },
                keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Email),
                colors = TextFieldDefaults.colors(
                    unfocusedContainerColor = colorResource(id = R.color.white),
                    focusedContainerColor = colorResource(id = R.color.white),
                    focusedIndicatorColor = colorResource(id = R.color.dark_blue),
                    unfocusedIndicatorColor = if (isError) Color.Red else Color.Gray,
                    unfocusedTextColor = Color.Gray,
                    focusedLabelColor = Color.Black
                ),
                modifier = Modifier.fillMaxWidth()
            )

            Spacer(modifier = Modifier.height(16.dp))

            OutlinedTextField(
                value = password,
                onValueChange = { password = it },
                label = { Text("Password") },
                visualTransformation = if (passwordVisible) VisualTransformation.None else PasswordVisualTransformation(),
                keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Password),
                colors = TextFieldDefaults.colors(
                    unfocusedContainerColor = colorResource(id = R.color.white),
                    focusedContainerColor = colorResource(id = R.color.white),
                    focusedIndicatorColor = colorResource(id = R.color.dark_blue),
                    unfocusedIndicatorColor = if (isError) Color.Red else Color.Gray,
                    unfocusedTextColor = Color.Gray,
                    focusedLabelColor = Color.Black
                ),
                modifier = Modifier.fillMaxWidth(),
                trailingIcon = {
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
                }
            )

            Spacer(modifier = Modifier.height(24.dp))

            Button(
                onClick = {
                    viewModel.viewModelScope.launch {
                        val success = viewModel.tryLoginWithEmail(email, password)
                        if (success) {
                            navController.navigate("Homepage")
                        } else {
                            isError = true
                        }
                    }
                },
                modifier = Modifier.fillMaxWidth(),
                colors = ButtonDefaults.buttonColors(containerColor = colorResource(id = R.color.dark_blue))
            ) {
                Text("Login", color = Color.White)
            }

            Spacer(modifier = Modifier.height(16.dp))

            if (isError) {
                Text(
                    text = "Wrong E-Mail or Password",
                    color = Color.Red,
                    fontWeight = FontWeight.Bold,
                    modifier = Modifier.padding(bottom = 16.dp)
                )
            }

            TextButton(
                onClick = { navController.navigate("forgot_password") }
            ) {
                Text(
                    text = "Forgot password?",
                    color = colorResource(id = R.color.dark_blue),
                )
            }
        }
    }
}