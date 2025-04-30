package com.dhkim.gamsahanilsang.presentation.notification

import android.content.Context
import androidx.work.Worker
import androidx.work.WorkerParameters
import com.dhkim.gamsahanilsang.domain.model.NotificationData
import com.dhkim.gamsahanilsang.domain.notification.NotificationManager
import dagger.assisted.Assisted
import dagger.assisted.AssistedFactory
import dagger.assisted.AssistedInject

class NotificationWorker @AssistedInject constructor(
    @Assisted context: Context,
    @Assisted workerParams: WorkerParameters,
    private val notificationManager: NotificationManager,
    private val notificationScheduler: NotificationScheduler
) : Worker(context, workerParams) {

    override fun doWork(): Result {
        val id = inputData.getInt("notification_id", 0)
        val title = inputData.getString("title") ?: "알림"
        val message = inputData.getString("message") ?: ""
        val channelId = inputData.getString("channel_id") ?: ""

        val notificationData = NotificationData(id, title, message, channelId)

        // 알림 표시
        notificationManager.showNotification(notificationData)

        // 다음 날 같은 시간에 다시 알림 예약
        notificationScheduler.scheduleDailyGratitudeNotification(hour = 21, minute = 0)

        return Result.success()
    }

    @AssistedFactory
    interface Factory {
        fun create(
            @Assisted context: Context,
            @Assisted workerParams: WorkerParameters
        ): NotificationWorker
    }
}