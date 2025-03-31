package com.dhkim.gamsahanilsang.data.repository

import com.dhkim.gamsahanilsang.domain.entity.GratitudeItem
import com.dhkim.gamsahanilsang.domain.repository.GratitudeRepository

class InMemoryGratitudeRepository : GratitudeRepository {
    private val gratitudeList = mutableListOf<GratitudeItem>()
    private var currentId = 1L

    override fun saveGratitude(item: GratitudeItem) {
        val newItem = item.copy(id = currentId++)
        gratitudeList.add(newItem)
    }

    override fun getAllGratitudes(): List<GratitudeItem> {
        return gratitudeList.toList()
    }

}