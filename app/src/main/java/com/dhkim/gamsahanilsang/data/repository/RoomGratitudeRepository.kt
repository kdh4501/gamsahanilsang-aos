package com.dhkim.gamsahanilsang.data.repository

import com.dhkim.gamsahanilsang.data.dao.GratitudeDao
import com.dhkim.gamsahanilsang.domain.entity.GratitudeItem
import com.dhkim.gamsahanilsang.domain.repository.GratitudeRepository
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext

class RoomGratitudeRepository(private val dao: GratitudeDao) : GratitudeRepository {
    override suspend fun saveGratitude(item: GratitudeItem) {
        withContext(Dispatchers.IO) {
            dao.insert(item)
        }
    }

    override suspend fun getAllGratitudes(): List<GratitudeItem> {
        return withContext(Dispatchers.IO) {
            dao.getAll()
        }
    }

    override suspend fun update(item: GratitudeItem) {
        dao.update(item)
    }

    override suspend fun deleteAllGratitude() {
        dao.deleteAll()
    }
}