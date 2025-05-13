package com.dhkim.gamsahanilsang.data.dao

import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.Query
import androidx.room.Update
import com.dhkim.gamsahanilsang.domain.entity.GratitudeItem
import kotlinx.coroutines.flow.Flow

@Dao
interface GratitudeDao {
    @Insert
    suspend fun insert(gratitudeItem: GratitudeItem)

    @Query("SELECT * FROM gratitude_items")
    suspend fun getAll(): List<GratitudeItem>

    @Update
    suspend fun update(item: GratitudeItem)

    @Delete
    suspend fun delete(item: GratitudeItem)

    @Query("DELETE FROM gratitude_items")
    suspend fun deleteAll()

    @Query("SELECT * FROM gratitude_items WHERE gratitudeText LIKE '%' || :query || '%' ORDER BY date DESC")
    fun searchGratitudes(query: String): List<GratitudeItem>

    @Query("""
        SELECT * FROM gratitude_items
        WHERE (:startDate IS NULL OR date >= :startDate)
        AND (:endDate IS NULL OR date <= :endDate)
        AND (:keyword IS NULL OR gratitudeText LIKE '%' || :keyword || '%')
        ORDER BY
        CASE WHEN :sortOrder = 'date DESC' THEN date END DESC,
        CASE WHEN :sortOrder = 'date ASC' THEN date END ASC,
        CASE WHEN :sortOrder = 'gratitudeText ASC' THEN gratitudeText END ASC
    """)
    fun getFilterGratitudeItems(
        startDate: String?,
        endDate: String?,
        keyword: String?,
        sortOrder: String
    ): Flow<List<GratitudeItem>>
}