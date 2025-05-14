package com.dhkim.gamsahanilsang.domain.entity

import androidx.room.Entity
import androidx.room.PrimaryKey
import com.dhkim.gamsahanilsang.utils.DateUtils

@Entity(tableName = "gratitude_items")
data class GratitudeItem(
    @PrimaryKey(autoGenerate = true) val id: Long = 0,
    val gratitudeText: String,
    val date: String = DateUtils.getTodayDate()
)