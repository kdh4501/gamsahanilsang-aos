package com.dhkim.gamsahanilsang.presentation.notification

import android.content.Context
import android.util.Log
import androidx.work.Worker
import androidx.work.WorkerParameters
import com.dhkim.gamsahanilsang.data.notification.NotificationManagerImpl
import com.dhkim.gamsahanilsang.domain.model.NotificationData
import com.dhkim.gamsahanilsang.domain.notification.NotificationManager
import dagger.assisted.Assisted
import dagger.assisted.AssistedFactory
import dagger.assisted.AssistedInject

class NotificationWorker @AssistedInject constructor(
    @Assisted context: Context,
    @Assisted workerParams: WorkerParameters
) : Worker(context, workerParams) {

    // 의존성을 지연 초기화하여 테스트에서 대체 가능하게 함
    private val notificationManager: NotificationManager by lazy {
        NotificationManagerImpl(applicationContext)
    }

    private val notificationScheduler: NotificationScheduler by lazy {
        NotificationScheduler(applicationContext)
    }

    override fun doWork(): Result {
        val id = inputData.getInt("notification_id", 0)
        val title = inputData.getString("title") ?: "알림"
        val message = inputData.getString("message") ?: ""
        val channelId = inputData.getString("channel_id") ?: ""

        val notificationData = NotificationData(id, title, message, channelId)

        // 로그 추가
        Log.d("NotificationWorker", "doWork: 알림 표시 시도 - $title, $message")

        // 알림 표시
        notificationManager.showNotification(notificationData)

        // 다음 날 같은 시간에 다시 알림 예약
        notificationScheduler.scheduleDailyGratitudeNotification(hour = 21, minute = 0)

        // 로그 추가
        Log.d("NotificationWorker", "doWork: 알림 표시 완료")

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