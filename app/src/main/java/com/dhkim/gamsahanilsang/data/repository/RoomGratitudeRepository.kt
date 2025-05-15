package com.dhkim.gamsahanilsang.data.repository

import com.dhkim.gamsahanilsang.data.dao.GratitudeDao
import com.dhkim.gamsahanilsang.domain.entity.GratitudeItem
import com.dhkim.gamsahanilsang.domain.model.GratitudeFilter
import com.dhkim.gamsahanilsang.domain.model.SortOrder
import com.dhkim.gamsahanilsang.domain.repository.GratitudeRepository
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.withContext
import java.time.format.DateTimeFormatter

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

    override suspend fun searchGratitudes(query: String): List<GratitudeItem> {
        return withContext(Dispatchers.IO) {
            gratitudeDao.searchGratitudes(query)
        }
    }

    override suspend fun getFilteredGratitudeItems(filter: GratitudeFilter): Flow<List<GratitudeItem>> {

        // 필터 파라미터 변환
        val startDate = filter.dateRange?.startDate?.format(DateTimeFormatter.ISO_LOCAL_DATE)
        val endDate = filter.dateRange?.endDate?.format(DateTimeFormatter.ISO_LOCAL_DATE)
        val keyword = filter.keyword

        // 정렬 순서 문자열로 변환
        val sortOrderString = when (filter.sortOrder) {
            SortOrder.NEWEST_FIRST -> "NEWEST_FIRST"
            SortOrder.OLDEST_FIRST -> "OLDEST_FIRST"
            SortOrder.ALPHABETICAL -> "ALPHABETICAL"
        }

        return gratitudeDao.getFilterGratitudeItems(
            startDate = startDate,
            endDate = endDate,
            keyword = keyword,
            sortOrder = sortOrderString
        )
    }


}