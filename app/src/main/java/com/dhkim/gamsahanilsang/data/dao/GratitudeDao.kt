package com.dhkim.gamsahanilsang.data.dao

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.Query
import androidx.room.Update
import com.dhkim.gamsahanilsang.domain.entity.GratitudeItem

@Dao
interface GratitudeDao {
    @Insert
    suspend fun insert(gratitudeItem: GratitudeItem)

    @Query("SELECT * FROM gratitude_items")
    suspend fun getAll(): List<GratitudeItem>

    @Update
    suspend fun update(item: GratitudeItem)
}