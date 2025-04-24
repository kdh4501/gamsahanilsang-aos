package com.dhkim.gamsahanilsang.presentation.di

import android.content.Context
import com.dhkim.gamsahanilsang.data.dao.GratitudeDao
import com.dhkim.gamsahanilsang.data.database.AppDatabase
import com.dhkim.gamsahanilsang.data.repository.RoomGratitudeRepository
import com.dhkim.gamsahanilsang.domain.repository.GratitudeRepository
import com.dhkim.gamsahanilsang.domain.usecase.GratitudeUseCase
import com.dhkim.gamsahanilsang.utils.DialogManager
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
object AppModule {

    @Provides
    @Singleton
    fun provideDatabase(@ApplicationContext context: Context): AppDatabase {
        return AppDatabase.getDatabase(context)
    }

    @Provides
    fun provideGratitudeDao(database: AppDatabase): GratitudeDao {
        return database.gratitudeDao()
    }

    @Provides
    @Singleton
    fun provideGratitudeRepository(gratitudeDao: GratitudeDao): GratitudeRepository {
        return RoomGratitudeRepository(gratitudeDao)
    }

    @Provides
    fun provideGratitudeUseCase(repository: GratitudeRepository): GratitudeUseCase {
        return GratitudeUseCase(repository)
    }

    @Provides
    fun provideDialogManager(@ApplicationContext context: Context): DialogManager {
        return DialogManager(context)
    }
}