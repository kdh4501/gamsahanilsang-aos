package com.dhkim.gamsahanilsang.domain.repository

import com.dhkim.gamsahanilsang.domain.entity.GratitudeItem

interface GratitudeRepository {
    fun saveGratitude(item: GratitudeItem)
    fun getAllGratitudes(): List<GratitudeItem>
}