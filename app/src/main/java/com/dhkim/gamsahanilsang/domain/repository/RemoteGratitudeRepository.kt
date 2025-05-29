package com.dhkim.gamsahanilsang.domain.repository

import com.dhkim.gamsahanilsang.domain.model.GratitudeEntry
import kotlinx.coroutines.flow.Flow
import java.util.Date

interface RemoteGratitudeRepository {
    fun getGratitudeEntries(userId: String): Flow<List<GratitudeEntry>> // 기존 전체 목록 조회
    /**
     * 특정 사용자의 감사 기록을 기간으로 필터링하여 가져옵니다.
     * @param userId 사용자 ID
     * @param startDate 시작 날짜/시간 (null이면 시작 제한 없음)
     * @param endDate 종료 날짜/시간 (null이면 종료 제한 없음)
     * @return 기간 필터링된 GratitudeEntry 목록 Flow
     */
    fun getGratitudeEntriesByDateRange(
        userId: String,
        startDate: Date? = null,
        endDate: Date? = null
    ): Flow<List<GratitudeEntry>>
    suspend fun addGratitudeEntry(userId: String, entry: GratitudeEntry): Result<Unit>
    suspend fun updateGratitudeEntry(userId: String, entry: GratitudeEntry): Result<Unit>
    suspend fun deleteGratitudeEntry(userId: String, entryId: String): Result<Unit>
}