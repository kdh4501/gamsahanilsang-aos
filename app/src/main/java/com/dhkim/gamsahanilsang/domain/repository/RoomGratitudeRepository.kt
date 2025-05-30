package com.dhkim.gamsahanilsang.domain.repository

import com.dhkim.gamsahanilsang.domain.entity.GratitudeItem
import com.dhkim.gamsahanilsang.domain.model.GratitudeFilter
import kotlinx.coroutines.flow.Flow
import java.util.Date

interface RoomGratitudeRepository {
    suspend fun saveGratitude(item: GratitudeItem)
    suspend fun getAllGratitudes(): List<GratitudeItem>
    suspend fun update(item: GratitudeItem)
    suspend fun delete(item: GratitudeItem)
    suspend fun deleteAllGratitude()
    suspend fun searchGratitudes(searchQuery: String): List<GratitudeItem>
    // 필터링된 감사 항목을 가져오는 메서드 추가
    suspend fun getFilteredGratitudeItems(filter: GratitudeFilter): Flow<List<GratitudeItem>>

    // 👇👇👇 기간 필터링 기능 함수 추가 (Room 구현) 👇👇👇
    /**
     * Room DB에서 감사 기록을 기간으로 필터링하여 가져옵니다.
     * @param startDate 시작 날짜/시간 (null이면 시작 제한 없음)
     * @param endDate 종료 날짜/시간 (null이면 종료 제한 없음)
     * @return 기간 필터링된 GratitudeItem 목록 Flow
     */
    suspend fun getGratitudeItemsByDateRange(
        startDate: Date? = null,
        endDate: Date? = null
    ): Flow<List<GratitudeItem>> // Room 모델 반환 (DataSource 역할의 DAO 함수와 일치)
}