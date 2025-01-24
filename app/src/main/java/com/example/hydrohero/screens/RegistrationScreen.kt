package com.example.hydrohero.screens

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Shadow
import androidx.compose.ui.res.colorResource
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.text.input.VisualTransformation
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController
import com.example.hydrohero.R
import com.example.hydrohero.ui.theme.LoginViewModel

@Composable
fun RegistrationScreen(viewModel: LoginViewModel, navController: NavController) {
    var email by remember { mutableStateOf("") }
    var username by remember { mutableStateOf("") }
    var password by remember { mutableStateOf("") }
    var confirmPassword by remember { mutableStateOf("") }
    var password1Visible by remember { mutableStateOf(false) }
    var password2Visible by remember { mutableStateOf(false) }
    var isError by remember { mutableStateOf(false) }

    val errorMessage by viewModel.errorMessage.collectAsState()

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
                .padding(16.dp),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Center
        ) {
            Text(
                text = "Register",
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
                value = username,
                onValueChange = { username = it },
                label = { Text("Username") },
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
                visualTransformation = if (password1Visible) VisualTransformation.None else PasswordVisualTransformation(),
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
                    val visibilityIcon = if (password1Visible)
                        painterResource(id = R.drawable.baseline_visibility_24)
                    else
                        painterResource(id = R.drawable.baseline_visibility_off_24)

                    IconButton(onClick = { password1Visible = !password1Visible }) {
                        Icon(
                            painter = visibilityIcon,
                            contentDescription = if (password1Visible) "Hide password" else "Show password"
                        )
                    }
                }
            )

            Spacer(modifier = Modifier.height(16.dp))

            OutlinedTextField(
                value = confirmPassword,
                onValueChange = { confirmPassword = it },
                label = { Text("Confirm Password") },
                visualTransformation = if (password2Visible) VisualTransformation.None else PasswordVisualTransformation(),
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
                    val visibilityIcon = if (password2Visible)
                        painterResource(id = R.drawable.baseline_visibility_24)
                    else
                        painterResource(id = R.drawable.baseline_visibility_off_24)

                    IconButton(onClick = { password2Visible = !password2Visible }) {
                        Icon(
                            painter = visibilityIcon,
                            contentDescription = if (password2Visible) "Hide password" else "Show password"
                        )
                    }
                }
            )

            errorMessage?.let {
                Text(
                    text = it,
                    color = Color.Red,
                    fontWeight = FontWeight.Bold,
                    modifier = Modifier.padding(top = 16.dp)
                )
            }

            Spacer(modifier = Modifier.height(24.dp))

            Button(
                onClick = {
                    if (password == confirmPassword) {
                        viewModel.registerWithEmail(email, username, password)
                        viewModel.updateUsername(username)
                        navController.navigate("Homepage")
                    } else {
                        viewModel.setErrorMessage("Wrong password")
                    }
                },
                modifier = Modifier.fillMaxWidth(),
                colors = ButtonDefaults.buttonColors(containerColor = colorResource(id = R.color.dark_blue))
            ) {
                Text("Register", color = Color.White)
            }

            Spacer(modifier = Modifier.height(16.dp))

            TextButton(
                onClick = { navController.navigate("login") }
            ) {
                Text(
                    text = "Already have an account? Login",
                    color = colorResource(id = R.color.dark_blue)
                )
            }
        }
    }
}

@Composable
fun ForgotPasswordScreen(viewModel: LoginViewModel, navController: NavController) {
    var email by remember { mutableStateOf("") }
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
                .padding(16.dp),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Center
        ) {
            Text(
                text = "Forgot Password",
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

            Spacer(modifier = Modifier.height(24.dp))

            Button(
                onClick = {
                    viewModel.sendPasswordResetEmail(email)
                    navController.navigateUp()
                },
                modifier = Modifier.fillMaxWidth(),
                colors = ButtonDefaults.buttonColors(containerColor = colorResource(id = R.color.dark_blue))
            ) {
                Text("Send Reset Link", color = Color.White)
            }

            Spacer(modifier = Modifier.height(16.dp))

            TextButton(
                onClick = { navController.navigate("login") }
            ) {
                Text(
                    text = "Back to Login",
                    color = colorResource(id = R.color.dark_blue),
                )
            }
        }
    }
}