package com.dhkim.gamsahanilsang.domain.repository

import com.dhkim.gamsahanilsang.domain.model.GratitudeEntry
import kotlinx.coroutines.flow.Flow

interface RemoteGratitudeRepository {
    fun getGratitudeEntries(userId: String): Flow<List<GratitudeEntry>>
    suspend fun addGratitudeEntry(userId: String, entry: GratitudeEntry): Result<Unit>
    suspend fun updateGratitudeEntry(userId: String, entry: GratitudeEntry): Result<Unit>
    suspend fun deleteGratitudeEntry(userId: String, entryId: String): Result<Unit>
}