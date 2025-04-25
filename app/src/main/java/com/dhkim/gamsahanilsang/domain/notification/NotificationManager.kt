package com.dhkim.gamsahanilsang.domain.notification

import com.dhkim.gamsahanilsang.domain.model.NotificationData

interface NotificationManager {
    fun showNotification(notificationData: NotificationData)
}