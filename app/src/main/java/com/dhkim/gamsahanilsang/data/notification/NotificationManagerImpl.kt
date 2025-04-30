package com.dhkim.gamsahanilsang.data.notification

import android.app.PendingIntent
import android.content.Context
import android.content.Intent
import android.util.Log
import androidx.core.app.NotificationCompat
import com.dhkim.gamsahanilsang.R
import com.dhkim.gamsahanilsang.domain.model.NotificationData
import com.dhkim.gamsahanilsang.domain.notification.NotificationManager
import com.dhkim.gamsahanilsang.presentation.main.MainActivity

class NotificationManagerImpl(
    private val context: Context
) : NotificationManager {

    private val notificationManager =
        context.getSystemService(Context.NOTIFICATION_SERVICE) as android.app.NotificationManager

    override fun showNotification(notificationData: NotificationData) {
        // 앱 열기위한 intent 생성
        val intent = Intent(context, MainActivity::class.java).apply {
            // 중요: 이 플래그들은 앱이 이미 실행 중일 때 새 액티비티를 시작하지 않고
            // 기존 액티비티를 최상위로 가져옵니다
            flags = Intent.FLAG_ACTIVITY_CLEAR_TOP or Intent.FLAG_ACTIVITY_SINGLE_TOP

            // 알림에서 왔다는 것을 표시 (선택 사항)
            putExtra("from_notification", true)
            putExtra("notification_id", notificationData.id)
        }

        val pendingIntent = PendingIntent.getActivity(
            context,
            notificationData.id,
            intent,
            PendingIntent.FLAG_IMMUTABLE or PendingIntent.FLAG_UPDATE_CURRENT
        )

        val builder = NotificationCompat.Builder(context, notificationData.channelId)
            .setSmallIcon(R.drawable.ic_notification)
            .setContentTitle(notificationData.title)
            .setContentText(notificationData.message)
            .setPriority(NotificationCompat.PRIORITY_HIGH)
            .setContentIntent(pendingIntent)
            .setAutoCancel(true)

            .setStyle(NotificationCompat.BigTextStyle().bigText(notificationData.message))  // 알림스타일
            .setVibrate(longArrayOf(0, 250, 250, 250))  // 진동패턴

        try {
            notificationManager.notify(notificationData.id, builder.build())
            Log.d("NotificationManager", "알림 표시 성공")
        } catch (e: Exception) {
            Log.e("NotificationManager", "알림 표시 실패: ${e.message}", e)
        }
    }
}