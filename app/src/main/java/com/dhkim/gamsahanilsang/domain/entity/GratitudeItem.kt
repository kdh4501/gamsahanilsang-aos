package com.dhkim.gamsahanilsang.domain.entity

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "gratitude_items")
data class GratitudeItem(
    @PrimaryKey(autoGenerate = true) val id: Long = 0,
    val gratitudeText: String,
    val date: String = getTodayDate()
)

fun getTodayDate(): String {
    val formatter = java.text.SimpleDateFormat("yyyy-MM-dd", java.util.Locale.getDefault())
    return formatter.format(java.util.Date())
}