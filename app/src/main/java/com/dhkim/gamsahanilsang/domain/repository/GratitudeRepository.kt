package com.dhkim.gamsahanilsang.domain.repository

import com.dhkim.gamsahanilsang.domain.entity.GratitudeItem

interface GratitudeRepository {
    suspend fun saveGratitude(item: GratitudeItem)
    suspend fun getAllGratitudes(): List<GratitudeItem>
    suspend fun update(item: GratitudeItem)
    suspend fun deleteAllGratitude()
}