package com.dhkim.gamsahanilsang.presentation.notification

import android.content.Context
import androidx.work.ListenableWorker
import androidx.work.WorkerFactory
import androidx.work.WorkerParameters
import com.dhkim.gamsahanilsang.domain.notification.NotificationManager
import javax.inject.Inject

class NotificationWorkerFactory @Inject constructor(
    private val notificationManager: NotificationManager,
    private val notificationScheduler: NotificationScheduler
) : WorkerFactory() {
    override fun createWorker(
        appContext: Context,
        workerClassName: String,
        workerParameters: WorkerParameters
    ): ListenableWorker? {
        return when(workerClassName) {
            NotificationWorker::class.java.name ->
                NotificationWorker(
                    appContext,
                    workerParameters,
                    notificationManager,
                    notificationScheduler
                )
            else -> null
        }
    }

}