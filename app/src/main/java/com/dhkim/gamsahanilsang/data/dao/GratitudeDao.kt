package com.dhkim.gamsahanilsang.data.dao

import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import androidx.room.Update
import com.dhkim.gamsahanilsang.domain.entity.GratitudeItem
import kotlinx.coroutines.flow.Flow

@Dao
interface GratitudeDao {
    @Insert(onConflict = OnConflictStrategy.IGNORE) // 동일 ID 충돌 시 무시
    suspend fun insert(gratitudeItem: GratitudeItem)

    @Query("SELECT * FROM gratitude_items ORDER BY date DESC")  // date 필드 기준으로 최신순
    suspend fun getAll(): List<GratitudeItem>

    @Update
    suspend fun update(item: GratitudeItem)

    @Delete
    suspend fun delete(item: GratitudeItem)

    @Query("DELETE FROM gratitude_items")
    suspend fun deleteAll()

    @Query("SELECT * FROM gratitude_items WHERE gratitudeText LIKE '%' || :query || '%' ORDER BY date DESC")
    suspend fun searchGratitudes(query: String): List<GratitudeItem>

    @Query("""
        SELECT * FROM gratitude_items
        WHERE (:startDate IS NULL OR date >= :startDate)
        AND (:endDate IS NULL OR date <= :endDate)
        AND (:keyword IS NULL OR gratitudeText LIKE '%' || :keyword || '%')
        ORDER BY
        CASE :sortOrder
            WHEN 'NEWEST_FIRST' THEN date
            WHEN 'OLDEST_FIRST' THEN date
            ELSE gratitudeText
        END DESC,
        CASE :sortOrder
            WHEN 'OLDEST_FIRST' THEN 1
            ELSE 0
        END ASC
    """)
    fun getFilterGratitudeItems(
        startDate: String?,
        endDate: String?,
        keyword: String?,
        sortOrder: String
    ): Flow<List<GratitudeItem>>

    @Query("""
        SELECT * FROM gratitude_items
        WHERE (:startDate = '0000-01-01' OR date >= :startDate)
        AND (:endDate = '9999-12-31' OR date <= :endDate)
        AND (:keyword IS NULL OR :keyword = '' OR gratitudeText LIKE '%' || :keyword || '%')
        ORDER BY date DESC
    """)
    fun getFilterGratitudeItemsNewest(
        startDate: String,
        endDate: String,
        keyword: String?
    ): Flow<List<GratitudeItem>>

    @Query("""
        SELECT * FROM gratitude_items
        WHERE (:startDate = '0000-01-01' OR date >= :startDate)
        AND (:endDate = '9999-12-31' OR date <= :endDate)
        AND (:keyword IS NULL OR :keyword = '' OR gratitudeText LIKE '%' || :keyword || '%')
        ORDER BY date ASC
    """)
    fun getFilterGratitudeItemsOldest(
        startDate: String,
        endDate: String,
        keyword: String?
    ): Flow<List<GratitudeItem>>

    @Query("""
        SELECT * FROM gratitude_items
        WHERE (:startDate = '0000-01-01' OR date >= :startDate)
        AND (:endDate = '9999-12-31' OR date <= :endDate)
        AND (:keyword IS NULL OR :keyword = '' OR gratitudeText LIKE '%' || :keyword || '%')
        ORDER BY gratitudeText COLLATE NOCASE ASC
    """)
    fun getFilterGratitudeItemsAlphabetical(
        startDate: String,
        endDate: String,
        keyword: String?
    ): Flow<List<GratitudeItem>>

}