package com.dhkim.gamsahanilsang.data.repository

import com.dhkim.gamsahanilsang.data.datasource.remote.FirestoreGratitudeDataSource
import com.dhkim.gamsahanilsang.domain.model.GratitudeEntry
import com.dhkim.gamsahanilsang.domain.repository.RemoteGratitudeRepository
import kotlinx.coroutines.flow.Flow
import java.util.Date

class FirestoreGratitudeRepositoryImpl(
    // 생성자를 통해 Firestore DataSource 의존성 주입
    private val dataSource: FirestoreGratitudeDataSource
) : RemoteGratitudeRepository { // Firestore Repository 인터페이스 구현

    override fun getGratitudeEntries(userId: String): Flow<List<GratitudeEntry>> {
        return dataSource.getEntries(userId)
    }

    override fun getGratitudeEntriesByDateRange(
        userId: String,
        startDate: Date?,
        endDate: Date?
    ): Flow<List<GratitudeEntry>> {
        return getGratitudeEntriesByDateRange(userId, startDate, endDate)
    }

    override suspend fun addGratitudeEntry(
        userId: String,
        entry: GratitudeEntry
    ): Result<Unit> {
        return dataSource.addEntry(userId, entry)
    }

    override suspend fun updateGratitudeEntry(
        userId: String,
        entry: GratitudeEntry
    ): Result<Unit> {
        return dataSource.updateEntry(userId, entry)
    }

    override suspend fun deleteGratitudeEntry(
        userId: String,
        entryId: String
    ): Result<Unit> {
        return dataSource.deleteEntry(userId, entryId)
    }
}