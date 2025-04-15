package com.dhkim.gamsahanilsang.domain.usecase

import com.dhkim.gamsahanilsang.domain.entity.GratitudeItem
import com.dhkim.gamsahanilsang.domain.repository.GratitudeRepository

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
}