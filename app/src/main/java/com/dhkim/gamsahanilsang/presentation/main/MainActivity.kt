package com.dhkim.gamsahanilsang.presentation.main

import android.os.Build
import android.os.Bundle
import android.widget.Toast
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.viewModels
import androidx.annotation.RequiresExtension
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Search
import androidx.compose.material3.CenterAlignedTopAppBar
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.res.stringResource
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import com.dhkim.gamsahanilsang.R
import com.dhkim.gamsahanilsang.presentation.gratitude.GratitudeScreen
import com.dhkim.gamsahanilsang.presentation.screen.settings.SettingsScreen
import com.dhkim.gamsahanilsang.presentation.screen.stats.StatsScreen
import com.dhkim.gamsahanilsang.presentation.ui.components.BottomNavigationBar
import com.dhkim.gamsahanilsang.presentation.ui.theme.MyTheme
import com.dhkim.gamsahanilsang.presentation.viewModel.MainViewModel
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class MainActivity : ComponentActivity() {
    private val viewModel: MainViewModel by viewModels()

    @OptIn(ExperimentalMaterial3Api::class)
    @RequiresExtension(extension = Build.VERSION_CODES.S, version = 13)
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        setContent {
            MyTheme {
                val navController = rememberNavController() // NavController 생성
                var showInputArea by remember { mutableStateOf(false) }

                Scaffold(
                    topBar = {
                        CenterAlignedTopAppBar(
                            title = { Text(getString(R.string.title_main)) },
                            navigationIcon = {
                                IconButton(onClick = {
                                    Toast.makeText(applicationContext, "미구현", Toast.LENGTH_SHORT).show()
                                }) {
                                    Icon(
                                        imageVector = Icons.Default.Search,
                                        contentDescription = stringResource(R.string.description_search)
                                    )
                                }
                            },
                            actions = {
                                IconButton(onClick = {
                                    showInputArea = true
                                }) {
                                    Icon(
                                        imageVector = Icons.Default.Add,
                                        contentDescription = stringResource(R.string.description_contents_add)
                                    )
                                }
                            }
                        )
                    },
                    bottomBar = {
                        BottomNavigationBar(
                            currentScreen = navController.currentDestination?.route ?: "gratitudeList",
                            onNavigateToHome = {
                                // 홈화면 네비게이션 중복 이동 방지
                                if (navController.currentDestination?.route != "gratitudeList") {
                                    navController.navigate("gratitudeList")
                                }
                                               },
                            onNavigateToStats = { navController.navigate("stats") },
                            onNavigateToSettings = { navController.navigate("settings") }
                        )
                    }
                ) { paddingValues ->
                    // NavHost 경로 설정
                    NavHost(navController = navController, startDestination = "gratitudeList") {
                        // NavigationItem Route 설정
                        composable("gratitudeList") {
                            GratitudeScreen (
                                viewModel = viewModel,
                                paddingValues = paddingValues,
                                showInputArea = showInputArea,
                                onInputAreaHidden = { showInputArea = false }
                            )
                        }
                        composable("stats") { StatsScreen(viewModel) }
                        composable("settings") { SettingsScreen() }
                    }
                }
            }
        }
    }
}