package com.dhkim.gamsahanilsang.domain.repository

import com.dhkim.gamsahanilsang.domain.entity.GratitudeItem
import com.dhkim.gamsahanilsang.domain.model.GratitudeFilter
import kotlinx.coroutines.flow.Flow

interface GratitudeRepository {
    suspend fun saveGratitude(item: GratitudeItem)
    suspend fun getAllGratitudes(): List<GratitudeItem>
    suspend fun update(item: GratitudeItem)
    suspend fun delete(item: GratitudeItem)
    suspend fun deleteAllGratitude()
    suspend fun searchGratitudes(searchQuery: String): List<GratitudeItem>
    // 필터링된 감사 항목을 가져오는 메서드 추가
    fun getFilteredGratitudeItems(filter: GratitudeFilter): Flow<List<GratitudeItem>>
}