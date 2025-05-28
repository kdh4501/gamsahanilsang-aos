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
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.core.content.ContextCompat
import com.dhkim.gamsahanilsang.presentation.navigation.AppNavigation
import com.dhkim.gamsahanilsang.presentation.notification.NotificationScheduler
import com.dhkim.gamsahanilsang.presentation.ui.theme.MyTheme
import com.dhkim.gamsahanilsang.presentation.viewModel.AuthViewModel
import com.dhkim.gamsahanilsang.presentation.viewModel.MainViewModel
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class MainActivity : ComponentActivity() {
    private val viewModel: MainViewModel by viewModels()
    private val authViewModel: AuthViewModel by viewModels()
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
        lifecycle.addObserver(authViewModel)
        notificationScheduler = NotificationScheduler(applicationContext)
        checkAndRequestNotificationPermission()

        setContent {
            MyTheme {
                AppNavigation(authViewModel = authViewModel)
            }
        }
    }

    // Activity가 종료될 때 ViewModel의 onCleared가 호출됩니다.
    override fun onDestroy() {
        super.onDestroy()
        // onCleared()에서 리스너 제거 로직이 있으므로 여기서 별도 제거는 필수는 아닐 수 있습니다.
        // 하지만 명확하게 제거하려면 여기서 removeObserver(authViewModel) 호출 가능
        lifecycle.removeObserver(authViewModel) // ViewModel 생명주기보다 먼저 종료될 때 안전하게 제거
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