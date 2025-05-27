package com.dhkim.gamsahanilsang.data.repository

import com.dhkim.gamsahanilsang.data.dao.GratitudeDao
import com.dhkim.gamsahanilsang.domain.entity.GratitudeItem
import com.dhkim.gamsahanilsang.domain.model.GratitudeFilter
import com.dhkim.gamsahanilsang.domain.repository.LocalGratitudeRepository
import kotlinx.coroutines.flow.Flow

// Room 기반 감사 기록 레포지토리 구현체
class RoomGratitudeRepositoryImpl(
    // 생성자를 통해 Room DAO 의존성 주입
    private val gratitudeDao: GratitudeDao
) : LocalGratitudeRepository  {
    override suspend fun saveGratitude(item: GratitudeItem) {
        gratitudeDao.insert(item)
    }

    override suspend fun getAllGratitudes(): List<GratitudeItem> {
        return gratitudeDao.getAll() // TODO: DAO의 getAll을 Flow<List<GratitudeItem>>으로 바꾸고 여기서 collect 또는 map 고려
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

    override suspend fun searchGratitudes(searchQuery: String): List<GratitudeItem> {
        return gratitudeDao.searchGratitudes(searchQuery) // TODO: DAO의 searchGratitudes도 Flow로 바꾸고 여기서 collect 또는 map 고려
    }

    override suspend fun getFilteredGratitudeItems(filter: GratitudeFilter): Flow<List<GratitudeItem>> {
        // 필터 객체의 내용을 파싱해서 DAO의 특정 필터 함수 호출 (네 기존 복잡한 쿼리 활용!)
        // TODO: filter 객체에 따라 getFilterGratitudeItemsNewest, getFilterGratitudeItemsOldest, getFilterGratitudeItemsAlphabetical 중 적절한 DAO 함수 호출
        val startDate = filter.dateRange?.startDate // 예시
        val endDate = filter.dateRange?.endDate // 예시
        val keyword = filter.keyword // 예시
        val sortOrder = filter.sortOrder // 예시

        return gratitudeDao.getFilterGratitudeItemsNewest(startDate.toString(),
            endDate.toString(), keyword) // 예시: 일단 Newest만 호출
        // 또는 기존 getFilterGratitudeItems 함수 사용
        // return gratitudeDao.getFilterGratitudeItems(startDate, endDate, keyword, sortOrder.name) // filter 객체와 DAO 함수의 인자 매핑 필요
    }

}