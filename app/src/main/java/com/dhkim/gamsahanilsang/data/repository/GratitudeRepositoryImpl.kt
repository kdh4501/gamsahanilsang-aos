package com.dhkim.gamsahanilsang.data.repository

import com.dhkim.gamsahanilsang.data.datasource.GratitudeDataSource
import com.dhkim.gamsahanilsang.domain.model.GratitudeEntry
import com.dhkim.gamsahanilsang.domain.repository.RemoteGratitudeRepository
import kotlinx.coroutines.flow.Flow

class GratitudeRepositoryImpl(
    // 생성자를 통해 DataSource 의존성을 주입받습니다.
    private val dataSource: GratitudeDataSource
) : RemoteGratitudeRepository {
    override fun getGratitudeEntries(userId: String): Flow<List<GratitudeEntry>> {
        return dataSource.getEntries(userId)
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