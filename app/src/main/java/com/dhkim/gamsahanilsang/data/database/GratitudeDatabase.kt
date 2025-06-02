package com.dhkim.gamsahanilsang.data.database

import androidx.room.Database
import androidx.room.RoomDatabase
import com.dhkim.gamsahanilsang.data.dao.GratitudeDao
import com.dhkim.gamsahanilsang.domain.entity.GratitudeItem

@Database(entities = [GratitudeItem::class], version = 1, exportSchema = false)
abstract class GratitudeDatabase : RoomDatabase() {
    abstract fun gratitudeDao(): GratitudeDao

}