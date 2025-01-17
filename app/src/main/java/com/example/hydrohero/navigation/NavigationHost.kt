package com.example.hydrohero.navigation

import androidx.activity.compose.BackHandler
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Icon
import androidx.compose.material3.NavigationBar
import androidx.compose.material3.NavigationBarItem
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavGraph.Companion.findStartDestination
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.currentBackStackEntryAsState
import androidx.navigation.compose.rememberNavController
import com.example.hydrohero.screens.AchievementsScreen
import com.example.hydrohero.screens.HistoryScreen
import com.example.hydrohero.screens.MainScreen
import com.example.hydrohero.screens.SettingsScreen
import com.example.hydrohero.screens.SocialScreen
import com.example.hydrohero.ui.theme.HydroHeroTheme
import com.example.hydrohero.Achievements
import com.example.hydrohero.History
import com.example.hydrohero.Overview
import com.example.hydrohero.Settings
import com.example.hydrohero.Social
import com.example.hydrohero.destinations
import com.example.hydrohero.ui.theme.MainViewModel
import com.example.hydrohero.ui.theme.MainViewModelFactory


@Composable
fun NavigationHost(viewModelFactory: MainViewModelFactory) {
    val navController = rememberNavController()
    val viewModel: MainViewModel = viewModel(factory = viewModelFactory)
    val currentBackStackEntry by navController.currentBackStackEntryAsState()
    val currentDestination = currentBackStackEntry?.destination
    val currentScreen = destinations.find { it.route == currentDestination?.route } ?: Overview

    HydroHeroTheme {
        Scaffold(
            bottomBar = {
                NavigationBar {
                    NavigationBarItem(
                        selected = currentScreen == Overview,
                        label = { Text(Overview.label) },
                        icon = { Icon(Overview.icon, contentDescription = "Overview Icon") },
                        onClick = {
                            navController.navigate(Overview.route) {
                                popUpTo(navController.graph.findStartDestination().id) {
                                    saveState = true
                                }
                                launchSingleTop = true
                                restoreState = true
                            }
                        }
                    )
                    NavigationBarItem(
                        selected = currentScreen == History,
                        label = { Text(History.label) },
                        icon = { Icon(History.icon, contentDescription = "History Icon") },
                        onClick = {
                            navController.navigate(History.route) {
                                popUpTo(navController.graph.findStartDestination().id) {
                                    saveState = true
                                }
                                launchSingleTop = true
                                restoreState = true
                            }
                        }
                    )
                    NavigationBarItem(
                        selected = currentScreen == Settings,
                        label = { Text(Settings.label) },
                        icon = { Icon(Settings.icon, contentDescription = "Settings Icon") },
                        onClick = {
                            navController.navigate(Settings.route) {
                                popUpTo(navController.graph.findStartDestination().id) {
                                    saveState = true
                                }
                                launchSingleTop = true
                                restoreState = true
                            }
                        }
                    )
                    NavigationBarItem(
                        selected = currentScreen == Social,
                        label = { Text(Social.label) },
                        icon = { Icon(Social.icon, contentDescription = "Socials Icon") },
                        onClick = {
                            navController.navigate(Social.route) {
                                popUpTo(navController.graph.findStartDestination().id) {
                                    saveState = true
                                }
                                launchSingleTop = true
                                restoreState = true
                            }
                        }
                    )
                    NavigationBarItem(
                        selected = currentScreen == Achievements,
                        label = { Text(Achievements.label) },
                        icon = { Icon(Achievements.icon, contentDescription = "Achievements Icon") },
                        onClick = {
                            navController.navigate(Achievements.route) {
                                popUpTo(navController.graph.findStartDestination().id) {
                                    saveState = true
                                }
                                launchSingleTop = true
                                restoreState = true
                            }
                        }
                    )
                }
            }
        ) { innerPadding ->
            NavHost(
                navController = navController,
                startDestination = Overview.route,
                modifier = Modifier
                    .fillMaxSize()
                    .padding(innerPadding),
            ) {
                composable(route = Overview.route) {
                    BackHandler {
                    }
                    MainScreen(viewModel)
                }
                composable(route = History.route) {
                    HistoryScreen(viewModel)
                }
                composable(route = Settings.route) {
                    SettingsScreen(viewModel)
                }
                composable(route = Social.route) {
                    SocialScreen(viewModel)
                }
                composable(route = Achievements.route) {
                    AchievementsScreen(viewModel)
                }
            }
        }
    }
}