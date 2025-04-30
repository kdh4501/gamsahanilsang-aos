package com.dhkim.gamsahanilsang.presentation.main

import android.content.pm.PackageManager
import android.os.Build
import android.os.Bundle
import android.widget.Toast
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.result.contract.ActivityResultContracts
import androidx.activity.viewModels
import androidx.annotation.RequiresExtension
import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Search
import androidx.compose.material3.CenterAlignedTopAppBar
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.core.content.ContextCompat
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import com.dhkim.gamsahanilsang.BuildConfig
import com.dhkim.gamsahanilsang.R
import com.dhkim.gamsahanilsang.presentation.gratitude.GratitudeScreen
import com.dhkim.gamsahanilsang.presentation.notification.NotificationScheduler
import com.dhkim.gamsahanilsang.presentation.screen.settings.SettingsScreen
import com.dhkim.gamsahanilsang.presentation.screen.stats.StatsScreen
import com.dhkim.gamsahanilsang.presentation.ui.components.BottomNavigationBar
import com.dhkim.gamsahanilsang.presentation.ui.theme.MyTheme
import com.dhkim.gamsahanilsang.presentation.viewModel.MainViewModel
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class MainActivity : ComponentActivity() {
    private val viewModel: MainViewModel by viewModels()
    private lateinit var notificationScheduler: NotificationScheduler
    @OptIn(ExperimentalMaterial3Api::class)
    @RequiresExtension(extension = Build.VERSION_CODES.S, version = 13)
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        /*
        // ⭐️ 테스트용 데이터
        val dateFormat = SimpleDateFormat("yyyy-MM-dd", Locale.getDefault())
        val today = Calendar.getInstance()
        val yesterday = Calendar.getInstance().apply { add(Calendar.DAY_OF_YEAR, -1) }
        val twoDaysAgo = Calendar.getInstance().apply { add(Calendar.DAY_OF_YEAR, -2) }

        // ✨ 테스트용 GratitudeItem 리스트 생성
        val testData = listOf(
            GratitudeItem(gratitudeText = "오늘 감사한 것", date = dateFormat.format(today.time)),
            GratitudeItem(gratitudeText = "어제 감사한 것", date = dateFormat.format(yesterday.time)),
            GratitudeItem(gratitudeText = "그저께 감사한 것", date = dateFormat.format(twoDaysAgo.time))
        )

        // ⭐️ calculateStreak 호출
        val streak = viewModel.calculateStreak(testData)

        // ⭐️ 결과를 Toast로 띄우기
        Toast.makeText(this, "🔥 테스트 Streak 결과: ${streak}일", Toast.LENGTH_LONG).show()
        */
        notificationScheduler = NotificationScheduler(applicationContext)
        checkAndRequestNotificationPermission()
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
                                // 테스트용 버튼
//                                Button(onClick = { sendTestNotification() }) {
//                                    Text(text = "알림 테스트")
//                                }
                            },
                            actions = {

                                // 테스트 버튼 추가 (10초 테스트)
                                if (BuildConfig.DEBUG) {
                                    TextButton(
                                        onClick = {
                                            notificationScheduler.scheduleTestNotificationSeconds(10)
                                            notificationScheduler.showTestNotificationImmediately()
                                            Toast.makeText(
                                                applicationContext,
                                                "10초 후에 알림이 발생합니다.",
                                                Toast.LENGTH_SHORT
                                            ).show()
                                        },
                                        modifier = Modifier.padding(end = 8.dp)
                                    ) {
                                        Text(
                                            text = "10초 테스트",
                                            color = MaterialTheme.colorScheme.primary,
                                            fontSize = 12.sp
                                        )
                                    }
                                }

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
                                } },
                            onNavigateToStats = {
                                if (navController.currentDestination?.route != "stats") {
                                    navController.navigate("stats")
                                } },
                            onNavigateToSettings = {
                                if (navController.currentDestination?.route != "settings") {
                                    navController.navigate("settings")
                                } }
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

//    private fun sendTestNotification() {
//        val notificationData = com.dhkim.gamsahanilsang.domain.model.NotificationData(
//            id = 9999,
//            title = "테스트 알림",
//            message = "알림 기능이 정상 동작합니다.",
//            channelId = Constants.NOTIFICATION_CHANNEL_ID
//        )
//
//        val notificationManager = NotificationManagerImpl(this)
//        notificationManager.showNotification(notificationData)
//    }

    private val requestNotificationPermissionLauncher =
        registerForActivityResult(ActivityResultContracts.RequestPermission()) { isGranted: Boolean ->
            if (isGranted) {
                Toast.makeText(this, "알림 권한 허용됨", Toast.LENGTH_SHORT).show()
            } else {
                Toast.makeText(this, "알림 권한 거부됨", Toast.LENGTH_SHORT).show()
            }
        }

    private fun checkAndRequestNotificationPermission() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
            if (ContextCompat.checkSelfPermission(this, android.Manifest.permission.POST_NOTIFICATIONS)
                != PackageManager.PERMISSION_GRANTED) {
                // 권한 요청
                requestNotificationPermissionLauncher.launch(android.Manifest.permission.POST_NOTIFICATIONS)
            }
        }
    }
}