package com.dhkim.gamsahanilsang.presentation.notification

import android.content.Context
import androidx.work.Worker
import androidx.work.WorkerParameters
import com.dhkim.gamsahanilsang.data.notification.NotificationManagerImpl
import com.dhkim.gamsahanilsang.domain.model.NotificationData

class NotificationWorker(
    context: Context,
    workerParams: WorkerParameters
) : Worker(context, workerParams) {

    override fun doWork(): Result {
        val id = inputData.getInt("notification_id", 0)
        val title = inputData.getString("title") ?: "알림"
        val message = inputData.getString("message") ?: ""
        val channelId = inputData.getString("channel_id") ?: ""

        val notificationData = NotificationData(id, title, message, channelId)

        // 의존성 주입이 어려우면 싱글톤 또는 서비스 locator 패턴 활용
        val notificationManager = NotificationManagerImpl(applicationContext)
        notificationManager.showNotification(notificationData)

        // 다음 날 동일 시간에 다시 알림 예약
        val scheduler = NotificationScheduler(applicationContext)
        scheduler.scheduleDailyGratitudeNotification(hour = 21, minute = 0)  // 예: 오후 9시

        return Result.success()
    }
}