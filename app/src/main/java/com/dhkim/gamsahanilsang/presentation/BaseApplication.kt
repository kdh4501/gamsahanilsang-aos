package com.dhkim.gamsahanilsang.presentation

import android.app.Application
import android.app.NotificationChannel
import android.app.NotificationManager
import com.dhkim.gamsahanilsang.R
import com.dhkim.gamsahanilsang.presentation.notification.NotificationScheduler
import com.dhkim.gamsahanilsang.utils.Constants
import dagger.hilt.android.HiltAndroidApp

@HiltAndroidApp
class BaseApplication : Application() {
    override fun onCreate() {
        super.onCreate()
        createNotificationChannel()

        scheduleNotification()  // 매일 저녁9시 알림 호출
    }

    private fun createNotificationChannel() {
        val channel = NotificationChannel(
            Constants.NOTIFICATION_CHANNEL_ID,
            getString(R.string.daily_notification_title),
            NotificationManager.IMPORTANCE_HIGH
        ).apply {
            description = getString(R.string.daily_notification_message)
        }

        val notificationManager =
            getSystemService(NotificationManager::class.java)
        notificationManager.createNotificationChannel(channel)
    }

    private fun scheduleNotification() {
        val notificationScheduler = NotificationScheduler(applicationContext)
        notificationScheduler.scheduleDailyGratitudeNotification(21, 0)
    }
}