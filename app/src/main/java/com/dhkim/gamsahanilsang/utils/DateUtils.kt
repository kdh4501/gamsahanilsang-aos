package com.dhkim.gamsahanilsang.utils

import java.text.SimpleDateFormat
import java.util.Calendar
import java.util.Date
import java.util.Locale

object DateUtils {
    /**
     * "yyyy-MM-dd" 형식의 날짜 문자열을 받아서
     * 오늘 날짜면 "오늘 M.d" 형식으로,
     * 아니면 "요일 M.d" 형식으로 반환
     * 예) "2025-04-10" -> "목 4.10", 오늘이면 "오늘 4.16"
     */
    fun formatDateLabel(dateString: String): String {
        val inputFormat = SimpleDateFormat("yyyy-MM-dd", Locale.getDefault())
        val date: Date? = try {
            inputFormat.parse(dateString)
        } catch (e: Exception) {
            null
        }

        if (date == null) return dateString // 파싱 실패 시 원본 반환

        val today = Calendar.getInstance()
        val target = Calendar.getInstance().apply { time = date }

        val isToday = today.get(Calendar.YEAR) == target.get(Calendar.YEAR) &&
                today.get(Calendar.DAY_OF_YEAR) == target.get(Calendar.DAY_OF_YEAR)

        return if (isToday) {
            val monthDayFormat = SimpleDateFormat("M.d", Locale.getDefault())
            "오늘 ${monthDayFormat.format(date)}"
        } else {
            val dayOfWeekFormat = SimpleDateFormat("E", Locale.getDefault()) // 요일 (ex: 목)
            val monthDayFormat = SimpleDateFormat("M.d", Locale.getDefault())
            "${dayOfWeekFormat.format(date)} ${monthDayFormat.format(date)}"
        }
    }
}