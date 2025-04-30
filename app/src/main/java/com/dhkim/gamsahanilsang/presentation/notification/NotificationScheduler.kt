package com.dhkim.gamsahanilsang.presentation.notification

import android.content.Context
import androidx.work.ExistingWorkPolicy
import androidx.work.OneTimeWorkRequestBuilder
import androidx.work.WorkManager
import androidx.work.workDataOf
import com.dhkim.gamsahanilsang.R
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
    fun scheduleTestNotification(delayMinutes: Int = 1) {
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
            .setInitialDelay(delayMinutes.toLong(), TimeUnit.MINUTES)
            .setInputData(data)
            .build()

        workManager.enqueueUniqueWork(
            "test_notification",
            ExistingWorkPolicy.REPLACE,
            notificationWork
        )
    }
}