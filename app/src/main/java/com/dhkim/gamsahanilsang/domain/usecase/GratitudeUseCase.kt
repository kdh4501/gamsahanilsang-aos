package com.dhkim.gamsahanilsang.domain.usecase

import android.util.Log
import com.dhkim.gamsahanilsang.domain.entity.GratitudeItem
import com.dhkim.gamsahanilsang.domain.model.GratitudeFilter
import com.dhkim.gamsahanilsang.domain.repository.GratitudeRepository
import kotlinx.coroutines.flow.Flow

class GratitudeUseCase(private val repository: GratitudeRepository) {
    suspend fun execute(gratitudeItem: GratitudeItem) {
        repository.saveGratitude(gratitudeItem)
    }

    suspend fun getAllGratitudes(): List<GratitudeItem> {
        return repository.getAllGratitudes()
    }

    suspend fun update(item: GratitudeItem) {
        repository.update(item)
    }

    suspend fun delete(item: GratitudeItem) {
        repository.delete(item)
    }

    suspend fun deleteAllGratitude() {
        repository.deleteAllGratitude()
    }

    suspend fun searchGratitudes(query: String): List<GratitudeItem> {
        return repository.searchGratitudes(query)
    }

    // 필터링된 감사 항목을 가져오는 메서드 추가
    suspend fun getFilteredGratitudeItems(filter: GratitudeFilter): Flow<List<GratitudeItem>> {
        Log.d("FilterDebug", "UseCase - 필터: $filter")
        return repository.getFilteredGratitudeItems(filter)
    }
}