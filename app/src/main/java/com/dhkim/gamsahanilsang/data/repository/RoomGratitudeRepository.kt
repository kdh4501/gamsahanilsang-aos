package com.dhkim.gamsahanilsang.data.repository

import com.dhkim.gamsahanilsang.data.dao.GratitudeDao
import com.dhkim.gamsahanilsang.domain.entity.GratitudeItem
import com.dhkim.gamsahanilsang.domain.repository.GratitudeRepository
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext

class RoomGratitudeRepository(private val gratitudeDao: GratitudeDao) : GratitudeRepository {
    override suspend fun saveGratitude(item: GratitudeItem) {
        withContext(Dispatchers.IO) {
            gratitudeDao.insert(item)
        }
    }

    override suspend fun getAllGratitudes(): List<GratitudeItem> {
        return withContext(Dispatchers.IO) {
            gratitudeDao.getAll()
        }
    }

    override suspend fun update(item: GratitudeItem) {
        gratitudeDao.update(item)
    }

    override suspend fun delete(item: GratitudeItem) {
        gratitudeDao.delete(item)
    }

    override suspend fun deleteAllGratitude() {
        gratitudeDao.deleteAll()
    }
}