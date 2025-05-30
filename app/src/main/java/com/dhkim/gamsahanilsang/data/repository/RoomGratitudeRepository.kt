package com.dhkim.gamsahanilsang.data.repository

import android.util.Log
import com.dhkim.gamsahanilsang.data.dao.GratitudeDao
import com.dhkim.gamsahanilsang.domain.entity.GratitudeItem
import com.dhkim.gamsahanilsang.domain.model.GratitudeFilter
import com.dhkim.gamsahanilsang.domain.model.SortOrder
import com.dhkim.gamsahanilsang.domain.repository.RoomGratitudeRepository
import com.dhkim.gamsahanilsang.utils.DateUtils
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.withContext
import java.time.format.DateTimeFormatter
import java.time.temporal.TemporalAccessor
import java.util.Date

class RoomGratitudeRepository(private val gratitudeDao: GratitudeDao) : RoomGratitudeRepository {
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

    override suspend fun getGratitudeItemsByDateRange(
        startDate: Date?,
        endDate: Date?
    ): Flow<List<GratitudeItem>> {
        // Date 객체를 Room DAO가 사용하는 String 포맷으로 변환
        // null이면 DAO 기본값에 맞게 null 또는 빈 날짜 문자열 전달 고려
        val startDateStr = startDate?.let { DateUtils.ROOM_DATE_FORMATTER.format(it as TemporalAccessor?) } // Date -> String 변환
        val endDateStr = endDate?.let { DateUtils.ROOM_DATE_FORMATTER.format(it as TemporalAccessor?) } // Date -> String 변환

        // TODO: Room DAO의 적절한 필터링 함수 호출
        // GratitudeDao의 getFilterGratitudeItemsNewest, getFilterGratitudeItemsOldest, getFilterGratitudeItemsAlphabetical
        // 또는 getFilterGratitudeItems 함수를 활용합니다.
        // 각 DAO 함수는 startDate, endDate, keyword를 String?으로 받습니다.
        // 여기서는 getFilterGratitudeItemsNewest 함수를 호출한다고 가정하며, keyword는 null로 전달합니다.
        return gratitudeDao.getFilterGratitudeItemsNewest(
            startDate = startDateStr ?: "0000-01-01", // DAO 기본값 고려
            endDate = endDateStr ?: "9999-12-31", // DAO 기본값 고려
            keyword = null // 기간 필터만 적용하므로 키워드는 null
        )
    }


}