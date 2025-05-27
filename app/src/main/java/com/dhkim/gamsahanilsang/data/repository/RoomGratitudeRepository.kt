package com.dhkim.gamsahanilsang.data.repository

import android.util.Log
import com.dhkim.gamsahanilsang.data.dao.GratitudeDao
import com.dhkim.gamsahanilsang.domain.entity.GratitudeItem
import com.dhkim.gamsahanilsang.domain.model.GratitudeFilter
import com.dhkim.gamsahanilsang.domain.model.SortOrder
import com.dhkim.gamsahanilsang.domain.repository.LocalGratitudeRepository
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.withContext
import java.time.format.DateTimeFormatter

class RoomGratitudeRepository(private val gratitudeDao: GratitudeDao) : LocalGratitudeRepository {
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
        // 날짜 범위가 NULL이면 최소/최대 날짜로 설정
        val startDate = filter.dateRange?.startDate?.format(DateTimeFormatter.ISO_LOCAL_DATE) ?: "0000-01-01"
        val endDate = filter.dateRange?.endDate?.format(DateTimeFormatter.ISO_LOCAL_DATE) ?: "9999-12-31"
        val keyword = filter.keyword

        return when (filter.sortOrder) {
            SortOrder.NEWEST_FIRST -> {
                Log.d("FilterDebug", "Repository - 최신순 정렬 적용")
                gratitudeDao.getFilterGratitudeItemsNewest(startDate, endDate, keyword)
            }
            SortOrder.OLDEST_FIRST -> {
                Log.d("FilterDebug", "Repository - 오래된순 정렬 적용")
                gratitudeDao.getFilterGratitudeItemsOldest(startDate, endDate, keyword)
            }
            SortOrder.ALPHABETICAL -> {
                Log.d("FilterDebug", "Repository - 알파벳순 정렬 적용")
                gratitudeDao.getFilterGratitudeItemsAlphabetical(startDate, endDate, keyword)
            }
        }
    }


}