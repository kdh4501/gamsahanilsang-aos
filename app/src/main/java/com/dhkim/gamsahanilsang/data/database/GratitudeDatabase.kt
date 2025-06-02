package com.dhkim.gamsahanilsang.data.database

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import com.dhkim.gamsahanilsang.data.dao.GratitudeDao
import com.dhkim.gamsahanilsang.domain.entity.GratitudeItem

@Database(entities = [GratitudeItem::class], version = 1, exportSchema = false)
abstract class GratitudeDatabase : RoomDatabase() {
    abstract fun gratitudeDao(): GratitudeDao

    companion object {
        @Volatile
        private var INSTANCE: GratitudeDatabase? = null

        fun getDatabase(context: Context): GratitudeDatabase {
            return INSTANCE ?: synchronized(this) {
                val instance = Room.databaseBuilder(
                    context.applicationContext,
                    GratitudeDatabase::class.java,
                    "gratitude_database"
                ).build()
                INSTANCE = instance
                instance
            }
        }
    }
}