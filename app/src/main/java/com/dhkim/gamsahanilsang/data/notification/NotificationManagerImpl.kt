package com.dhkim.gamsahanilsang.data.notification

import android.content.Context
import androidx.core.app.NotificationCompat
import com.dhkim.gamsahanilsang.R
import com.dhkim.gamsahanilsang.domain.model.NotificationData
import com.dhkim.gamsahanilsang.domain.notification.NotificationManager

class NotificationManagerImpl(
    private val context: Context
) : NotificationManager {

    private val notificationManager =
        context.getSystemService(Context.NOTIFICATION_SERVICE) as android.app.NotificationManager

    override fun showNotification(notificationData: NotificationData) {
        val builder = NotificationCompat.Builder(context, notificationData.channelId)
            .setSmallIcon(R.drawable.ic_launcher)
            .setContentTitle(notificationData.title)
            .setContentText(notificationData.message)
            .setPriority(NotificationCompat.PRIORITY_HIGH)
            .setAutoCancel(true)

        notificationManager.notify(notificationData.id,builder.build())
    }


}