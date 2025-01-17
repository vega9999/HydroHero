package com.example.hydrohero

import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.DateRange
import androidx.compose.material.icons.filled.Face
import androidx.compose.material.icons.filled.Home
import androidx.compose.material.icons.filled.Info
import androidx.compose.material.icons.filled.MoreVert
import androidx.compose.material.icons.filled.Person
import androidx.compose.material.icons.filled.Settings
import androidx.compose.material.icons.filled.Star
import androidx.compose.ui.graphics.vector.ImageVector

interface Destination {
    val label: String
    val icon: ImageVector
    val route: String
}

object Overview : Destination {
    override val label: String = "Overview"
    override val icon = Icons.Filled.Home
    override val route = "overview"
}

object History : Destination {
    override val label: String = "History "
    override val icon = Icons.Filled.DateRange
    override val route = "history"
}

object Settings : Destination {
    override val label: String = "Settings"
    override val icon = Icons.Filled.Settings
    override val route = "settings"
}

object Social : Destination {
    override val label: String = "Social"
    override val icon = Icons.Filled.Face
    override val route = "social"
}

object Achievements : Destination {
    override val label: String = "Achievements"
    override val icon = Icons.Filled.Star
    override val route = "achievements"
}

val destinations = listOf(
    Overview,
    History,
    Settings,
    Social,
    Achievements
)