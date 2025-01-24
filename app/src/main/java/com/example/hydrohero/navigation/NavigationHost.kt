package com.example.hydrohero.navigation

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Home
import androidx.compose.material.icons.filled.Menu
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.colorResource
import androidx.compose.ui.res.dimensionResource
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import kotlinx.coroutines.launch
import com.example.hydrohero.screens.EmailLoginScreen
import com.example.hydrohero.screens.ForgotPasswordScreen
import com.example.hydrohero.screens.HistoryScreen
import com.example.hydrohero.screens.LoginScreen
import com.example.hydrohero.screens.MainScreen
import com.example.hydrohero.screens.RegistrationScreen
import com.example.hydrohero.ui.theme.LoginViewModel
import com.example.hydrohero.ui.theme.MainViewModelFactory
import com.example.hydrohero.R
import com.example.hydrohero.database.DatabaseProvider
import com.example.hydrohero.screens.AchievementsScreen
import com.example.hydrohero.screens.SettingsScreen
import com.example.hydrohero.screens.SocialScreen
import com.example.hydrohero.ui.theme.MainViewModel

@Composable
fun NavigationHost(
    loginViewModel: LoginViewModel,
    mainViewModel: MainViewModel
) {
    val navController = rememberNavController()
    val context = LocalContext.current
    val waterIntakeDao = DatabaseProvider.provideWaterIntakeDao(context)
    val mainViewModelFactory = MainViewModelFactory(waterIntakeDao, context)

    val isLoggedIn by loginViewModel.isLoggedIn.collectAsState()
    val drawerState = rememberDrawerState(initialValue = DrawerValue.Closed)
    val scope = rememberCoroutineScope()

    val username = loginViewModel.username.collectAsState(initial = null)

    ModalNavigationDrawer(
        drawerContent = {
            ModalDrawerSheet {
                DrawerContent(
                    username = username.value ?: "Guest",
                    onMenuItemClick = { selectedScreen ->
                        scope.launch {
                            drawerState.close()
                            navController.navigate(selectedScreen)
                        }
                    },
                    onLogoutClick = {
                        scope.launch {
                            drawerState.close()
                            loginViewModel.logout()
                            navController.navigate("login") {
                                popUpTo(0) { inclusive = true }
                            }
                        }
                    },
                    viewModel = mainViewModel
                )
            }
        },
        drawerState = drawerState
    ) {
        NavHost(
            navController = navController,
            startDestination = if (isLoggedIn) "Homepage" else "login"
        ) {
            composable("login") {
                LoginScreen(
                    viewModel = loginViewModel,
                    navController = navController
                )
            }
            composable("email_login") {
                EmailLoginScreen(
                    viewModel = loginViewModel,
                    navController = navController
                )
            }
            composable("email_registration") {
                RegistrationScreen(
                    viewModel = loginViewModel,
                    navController = navController
                )
            }
            composable("forgot_password") {
                ForgotPasswordScreen(
                    viewModel = loginViewModel,
                    navController = navController
                )
            }
            composable("Homepage") {
                //val mainViewModel: MainViewModel = viewModel(factory = mainViewModelFactory)
                ScreenWithDrawer(
                    title = "Home",
                    drawerState = drawerState
                ) { paddingValues ->
                    MainScreen(
                        navController = navController,
                        viewModel = mainViewModel,
                        modifier = Modifier.padding(paddingValues)
                    )
                }
            }
            composable("History") {
                //val mainViewModel: MainViewModel = viewModel(factory = mainViewModelFactory)
                ScreenWithDrawer(
                    title = "History",
                    drawerState = drawerState
                ) { paddingValues ->
                    HistoryScreen(
                        viewModel = mainViewModel,
                        modifier = Modifier.padding(paddingValues)
                    )
                }
            }
            composable("Social") {
                //val mainViewModel: MainViewModel = viewModel(factory = mainViewModelFactory)
                ScreenWithDrawer(
                    title = "Social",
                    drawerState = drawerState
                ) { paddingValues ->
                    SocialScreen(
                        viewModel = mainViewModel,
                        modifier = Modifier.padding(paddingValues)
                    )
                }
            }
            composable("Settings") {
                //val mainViewModel: MainViewModel = viewModel(factory = mainViewModelFactory)
                ScreenWithDrawer(
                    title = "Settings",
                    drawerState = drawerState
                ) { paddingValues ->
                    SettingsScreen(
                        viewModel = mainViewModel,
                        modifier = Modifier.padding(paddingValues),
                        onBackPressed = { navController.popBackStack()}
                    )
                }
            }
            composable("Achievements") {
                //val mainViewModel: MainViewModel = viewModel(factory = mainViewModelFactory)
                ScreenWithDrawer(
                    title = "Achievements",
                    drawerState = drawerState
                ) { paddingValues ->
                    AchievementsScreen(
                        viewModel = mainViewModel,
                        modifier = Modifier.padding(paddingValues)
                    )
                }
            }
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ScreenWithDrawer(
    title: String,
    drawerState: DrawerState,
    content: @Composable (PaddingValues) -> Unit
) {
    val scope = rememberCoroutineScope()
    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text(title) },
                navigationIcon = {
                    IconButton(onClick = { scope.launch { drawerState.open() } }) {
                        Icon(Icons.Default.Menu, contentDescription = "Menu Icon")
                    }
                }
            )
        },
        content = content
    )
}

@Composable
fun DrawerContent(onMenuItemClick: (String) -> Unit, username: String, onLogoutClick: () -> Unit, viewModel: MainViewModel ) {
    val uiState by viewModel.uiState.collectAsState()
    val gender = uiState.userProfile.gender

    Column(
        modifier = Modifier
            .fillMaxHeight()
            .width(dimensionResource(id = R.dimen.navigation_drawer_width))
            .padding(dimensionResource(id = R.dimen.margin_medium_2))
    ) {
        Text(
            text = stringResource(id = R.string.app_name),
            fontSize = dimensionResource(id = R.dimen.text_heading_1x).value.sp,
            color = Color.Black,
            modifier = Modifier
                .padding(bottom = dimensionResource(id = R.dimen.margin_large))
                .align(Alignment.Start),
            style = MaterialTheme.typography.titleLarge
        )

        Spacer(modifier = Modifier.height(dimensionResource(id = R.dimen.margin_small)))

        Icon(
            painter = painterResource(
                id = if (gender == "male") R.drawable.ic_boy else R.drawable.ic_girl
            ),
            contentDescription = "Avatar",
            tint = Color.Unspecified,
            modifier = Modifier
                .size(80.dp)
                .padding(8.dp)
        )

        Text(
            text = "Welcome back",
            fontSize = 20.sp,
            fontWeight = FontWeight.Bold,
            color = Color.Black
        )
        Text(
            text = username,
            fontSize = 16.sp,
            color = Color.Gray
        )

        Spacer(modifier = Modifier.height(dimensionResource(id = R.dimen.margin_large)))

        DrawerMenuItem(
            text = stringResource(id = R.string.home_page_menu_item),
            iconRes = R.drawable.ic_home,
            onClick = { onMenuItemClick("Homepage") }
        )

        DrawerMenuItem(
            text = stringResource(id = R.string.history_menu_item),
            iconRes = R.drawable.ic_history,
            onClick = { onMenuItemClick("History") }
        )

        DrawerMenuItem(
            text = stringResource(id = R.string.social_menu_item),
            iconRes = R.drawable.ic_social,
            onClick = { onMenuItemClick("Social") }
        )

        DrawerMenuItem(
            text = stringResource(id = R.string.settings_menu_item),
            iconRes = R.drawable.ic_settings,
            onClick = { onMenuItemClick("Settings") }
        )
        DrawerMenuItem(
            text = stringResource(id = R.string.achievements_menu_item),
            iconRes = R.drawable.ic_achievements,
            onClick = { onMenuItemClick("Achievements") }
        )

        Spacer(modifier = Modifier.height(16.dp))

        Button(
            onClick = onLogoutClick,
            modifier = Modifier.align(Alignment.Start),
            colors = ButtonDefaults.buttonColors(containerColor = colorResource(id = R.color.dark_blue))
        ) {
            Text(text = "Logout", color = Color.White)
        }
    }
}

@Composable
fun DrawerMenuItem(text: String, iconRes: Int, onClick: () -> Unit) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = dimensionResource(id = R.dimen.drawer_menu_item_padding))
            .wrapContentHeight()
            .clickable { onClick() },
        verticalAlignment = Alignment.CenterVertically
    ) {
        Icon(
            painter = painterResource(id = iconRes),
            contentDescription = text,
            tint = colorResource(id = R.color.icons),
            modifier = Modifier.size(dimensionResource(id = R.dimen.margin_large))
        )
        Spacer(modifier = Modifier.width(dimensionResource(id = R.dimen.margin_medium_2)))
        Text(
            text = text,
            fontSize = dimensionResource(id = R.dimen.text_regular_2x).value.sp,
            color = colorResource(id = R.color.black),
            style = MaterialTheme.typography.bodyMedium
        )
    }
}