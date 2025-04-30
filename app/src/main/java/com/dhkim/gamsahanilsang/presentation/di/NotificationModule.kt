package com.dhkim.gamsahanilsang.presentation.di

import android.content.Context
import com.dhkim.gamsahanilsang.data.notification.NotificationManagerImpl
import com.dhkim.gamsahanilsang.domain.notification.NotificationManager
import com.dhkim.gamsahanilsang.presentation.notification.NotificationScheduler
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
object NotificationModule {

    @Provides
    @Singleton
    fun provideNotificationManager(@ApplicationContext context: Context): NotificationManager {
        return NotificationManagerImpl(context)
    }

    @Provides
    @Singleton
    fun provideNotifiationScheduler(@ApplicationContext context: Context): NotificationScheduler {
        return NotificationScheduler(context)
    }
}