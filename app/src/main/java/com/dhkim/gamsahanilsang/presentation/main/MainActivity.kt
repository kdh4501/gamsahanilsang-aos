package com.dhkim.gamsahanilsang.presentation.main

import android.os.Build
import android.os.Bundle
import android.widget.EditText
import android.widget.Toast
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.viewModels
import androidx.annotation.RequiresExtension
import androidx.appcompat.app.AlertDialog
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Clear
import androidx.compose.material.icons.filled.Edit
import androidx.compose.material.icons.filled.Search
import androidx.compose.material3.Button
import androidx.compose.material3.CenterAlignedTopAppBar
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.ListItem
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalSoftwareKeyboardController
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import com.dhkim.gamsahanilsang.R
import com.dhkim.gamsahanilsang.data.database.AppDatabase
import com.dhkim.gamsahanilsang.data.repository.RoomGratitudeRepository
import com.dhkim.gamsahanilsang.domain.entity.GratitudeItem
import com.dhkim.gamsahanilsang.domain.usecase.GratitudeUseCase
import com.dhkim.gamsahanilsang.presentation.screen.detil.DetailDialog
import com.dhkim.gamsahanilsang.presentation.screen.settings.SettingsScreen
import com.dhkim.gamsahanilsang.presentation.screen.stats.StatsScreen
import com.dhkim.gamsahanilsang.presentation.ui.components.BottomNavigationBar
import com.dhkim.gamsahanilsang.presentation.ui.theme.MyTheme
import com.dhkim.gamsahanilsang.presentation.viewModel.MainViewModel
import com.dhkim.gamsahanilsang.presentation.viewModel.MainViewModelFactory
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

class MainActivity : ComponentActivity() {
    private val gratitudeDao by lazy { AppDatabase.getDatabase(this).gratitudeDao() }
    private val viewModel: MainViewModel by viewModels { MainViewModelFactory(gratitudeUseCase) }

    private lateinit var gratitudeUseCase: GratitudeUseCase

    @OptIn(ExperimentalMaterial3Api::class)
    @RequiresExtension(extension = Build.VERSION_CODES.S, version = 13)
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        gratitudeUseCase = GratitudeUseCase(RoomGratitudeRepository(gratitudeDao))

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
                            GratitudeApp(
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

    @OptIn(ExperimentalMaterial3Api::class)
    @Composable
    fun GratitudeApp(
        viewModel: MainViewModel,
        paddingValues: PaddingValues,
        showInputArea: Boolean = false,
        onInputAreaHidden: (() -> Unit)? = null
    ) {
        var gratitudeText by remember { mutableStateOf("") }
        var showDialog by remember { mutableStateOf(false) }
        var selectedItem by remember { mutableStateOf<GratitudeItem?>(null) }

        val uiState by viewModel.uiState.collectAsState()
        val streak = uiState.streak
        val isStreakToastShown = uiState.isStreakToastShown

        val keyboardController = LocalSoftwareKeyboardController.current

        fun hideKeyboard() {
            keyboardController?.hide()
        }

        val todayStr = remember {
            SimpleDateFormat("yyyy-MM-dd", Locale.getDefault()).format(Date())
        }

        LaunchedEffect(key1 = todayStr) {
            if (!isStreakToastShown) {
                Toast.makeText(applicationContext,
                    getString(R.string.streak_toast_message, streak), Toast.LENGTH_SHORT).show()
                viewModel.markStreakToastShown()
            }
        }

        uiState.error?.let { errorMessage ->
            LaunchedEffect(errorMessage) {
                Toast.makeText(applicationContext, errorMessage, Toast.LENGTH_SHORT).show()
            }
        }

        Column(
            modifier = Modifier
                .padding(paddingValues)
                .fillMaxSize()
                .background(MaterialTheme.colorScheme.background)
                .padding(16.dp)
        ) {
            if (showInputArea) {
                OutlinedTextField(
                    value = gratitudeText,
                    onValueChange = { gratitudeText = it },
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(10.dp),
                    label = { Text(getString(R.string.input_hint)) },
                    trailingIcon = {
                        IconButton(onClick = { gratitudeText = "" }) {
                            Icon(Icons.Filled.Clear, contentDescription = "Clear")
                        }
                    }
                )

                Spacer(modifier = Modifier.height(8.dp))

                Button(
                    onClick = {
                        if (gratitudeText.isNotBlank()) {
                            viewModel.saveGratitude(gratitudeText)
                            gratitudeText = ""
                            hideKeyboard()
                            onInputAreaHidden?.invoke()
                        }
                    },
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Text(getString(R.string.save_button))
                }
            }

            Spacer(modifier = Modifier.height(16.dp))

            GratitudeList( {item ->
                selectedItem = item
                showDialog = true
            })

            if (showDialog && selectedItem != null) {
                DetailDialog(
                    showDialog = showDialog,
                    onDismiss = { showDialog = false; selectedItem = null },
                    item = selectedItem!!,
                    viewModel = viewModel
                )
            }
        }
    }

    @Composable
    fun GratitudeList(
        onItemClick: (GratitudeItem) -> Unit
    ) {
        val groupedItems by viewModel.groupedGratitudes.collectAsState()

        val uiState by viewModel.uiState.collectAsState()

        if (uiState.isLoading) {
            Box(
                modifier = Modifier.fillMaxSize(),
                contentAlignment = Alignment.Center
            ) {
                CircularProgressIndicator()
            }
        }

        LazyColumn {
            groupedItems.forEach { (date, gratitudeItems) ->
                item {
                    Text(
                        text = date,
                        style = MaterialTheme.typography.titleMedium,
                        modifier = Modifier.padding(top = 16.dp, bottom = 8.dp)
                    )
                }

                items(gratitudeItems) { item ->
                    ListItem(
                        modifier = Modifier
                            .fillMaxWidth()
                            .clickable { onItemClick(item) },
                        headlineContent = { Text(item.gratitudeText) },
                        trailingContent = {
                            IconButton(onClick = { showEditDialog(item) }) {
                                Icon(Icons.Filled.Edit, contentDescription = "Edit")
                            }
                        }
                    )
                }

            }
        }
    }

    private fun showEditDialog(item: GratitudeItem) {
        val editText = EditText(this).apply {
            setText(item.gratitudeText)
        }

        AlertDialog.Builder(this)
            .setTitle(getString(R.string.edit_button))
            .setView(editText)
            .setPositiveButton(getString(R.string.ok)) { _, _ ->
                val updateText = editText.text.toString()
                this.viewModel.updateGratitude(item.copy(gratitudeText = updateText))
            }
            .setNegativeButton(getString(R.string.cancel), null)
            .show()
    }
}