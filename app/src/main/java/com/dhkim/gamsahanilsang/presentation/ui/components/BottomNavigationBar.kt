package com.dhkim.gamsahanilsang.presentation.ui.components

import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Home
import androidx.compose.material.icons.filled.Settings
import androidx.compose.material.icons.filled.ShowChart
import androidx.compose.material3.Icon
import androidx.compose.material3.NavigationBar
import androidx.compose.material3.NavigationBarItem
import androidx.compose.runtime.Composable
import androidx.compose.ui.graphics.vector.ImageVector

@Composable
fun BottomNavigationBar(
    currentScreen: String,
    onNavigateToHome: () -> Unit,
    onNavigateToStats: () -> Unit,
    onNavigateToSettings: () -> Unit
) {
    NavigationBar {
        val items = listOf(
            NavigationItem.Home,
            NavigationItem.Stats,
            NavigationItem.Settings
        )

        items.forEach { item ->
            NavigationBarItem(
                icon = { Icon(imageVector = item.icon, contentDescription = null) },
                label = null,
                selected = currentScreen == item.route,
                onClick = {
                    when (item) {
                        NavigationItem.Home -> {
                            onNavigateToHome()
                        }
                        NavigationItem.Stats -> {
                            onNavigateToStats()
                        }
                        NavigationItem.Settings -> {
                            onNavigateToSettings()
                        }
                    }
                }
            )

        }

    }
}

sealed class NavigationItem(val route: String, val icon: ImageVector) {
    object Home : NavigationItem("gratitudeList", Icons.Filled.Home)
    object Stats : NavigationItem("stats", Icons.Filled.ShowChart)
    object Settings : NavigationItem("settings", Icons.Filled.Settings)
}