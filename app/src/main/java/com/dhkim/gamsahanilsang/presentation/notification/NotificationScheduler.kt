package com.dhkim.gamsahanilsang.presentation.notification

import android.content.Context
import android.util.Log
import androidx.work.ExistingWorkPolicy
import androidx.work.OneTimeWorkRequestBuilder
import androidx.work.WorkManager
import androidx.work.workDataOf
import com.dhkim.gamsahanilsang.R
import com.dhkim.gamsahanilsang.data.notification.NotificationManagerImpl
import com.dhkim.gamsahanilsang.domain.model.NotificationData
import com.dhkim.gamsahanilsang.utils.Constants
import java.util.Calendar
import java.util.concurrent.TimeUnit

class NotificationScheduler(
    private val context: Context,
) {

    fun scheduleDailyGratitudeNotification(hour: Int, minute: Int) {
        val workManager = WorkManager.getInstance(context)

        val currentDateTime = Calendar.getInstance()
        val dueDate = Calendar.getInstance().apply {
            set(Calendar.HOUR_OF_DAY, hour)
            set(Calendar.MINUTE, minute)
            set(Calendar.SECOND, 0)
            if (before(currentDateTime)) {
                add(Calendar.DAY_OF_MONTH, 1)
            }
        }

        val delay = dueDate.timeInMillis - currentDateTime.timeInMillis

        val notificationData = NotificationData(
            id = 1001,
            title = context.getString(R.string.daily_notification_title),
            message = context.getString(R.string.daily_notification_message),
            channelId = Constants.NOTIFICATION_CHANNEL_ID
        )

        val data = workDataOf(
            "notification_id" to notificationData.id,
            "title" to notificationData.title,
            "message" to notificationData.message,
            "channel_id" to notificationData.channelId
        )

        val notificationWork = OneTimeWorkRequestBuilder<NotificationWorker>()
            .setInitialDelay(delay, TimeUnit.MILLISECONDS)
            .setInputData(data)
            .build()

        workManager.enqueueUniqueWork(
            "daily_gratitude_notification",
            ExistingWorkPolicy.REPLACE,
            notificationWork
        )
    }

    // 테스트용 메서드
    fun scheduleTestNotificationSeconds(delaySeconds: Int = 10) {
        val workManager = WorkManager.getInstance(context)

        val notificationData = NotificationData(
            id = 1001,
            title = context.getString(R.string.daily_notification_title),
            message = context.getString(R.string.daily_notification_message),
            channelId = Constants.NOTIFICATION_CHANNEL_ID
        )

        val data = workDataOf(
            "notification_id" to notificationData.id,
            "title" to notificationData.title,
            "message" to notificationData.message,
            "channel_id" to notificationData.channelId
        )

        val notificationWork = OneTimeWorkRequestBuilder<NotificationWorker>()
            .setInitialDelay(delaySeconds.toLong(), TimeUnit.SECONDS)
            .setInputData(data)
            .build()

        workManager.enqueueUniqueWork(
            "test_notification_seconds",
            ExistingWorkPolicy.REPLACE,
            notificationWork
        )
    }

    fun showTestNotificationImmediately() {
        val notificationData = NotificationData(
            id = 1002,
            title = context.getString(R.string.daily_notification_title),
            message = context.getString(R.string.daily_notification_message),
            channelId = Constants.NOTIFICATION_CHANNEL_ID
        )

        val notificationManager = NotificationManagerImpl(context)
        notificationManager.showNotification(notificationData)

        Log.d("NotificationTest", "즉시 알림 표시 요청됨")
    }
}