package com.dhkim.gamsahanilsang.domain.model

import java.time.LocalDate

data class GratitudeFilter(
    val dateRange: DateRange? = null,
    val keyword: String? = null,
    val sortOrder: SortOrder = SortOrder.NEWEST_FIRST
)

data class DateRange(
    val startDate: LocalDate,
    val endDate: LocalDate
)

// 정렬 순서 열거형
enum class SortOrder {
    NEWEST_FIRST,
    OLDEST_FIRST,
    ALPHABETICAL
}
