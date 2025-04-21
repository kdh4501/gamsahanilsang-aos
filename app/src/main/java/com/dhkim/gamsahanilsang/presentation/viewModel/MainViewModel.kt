package com.dhkim.gamsahanilsang.presentation.viewModel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.dhkim.gamsahanilsang.domain.entity.GratitudeItem
import com.dhkim.gamsahanilsang.domain.usecase.GratitudeUseCase
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

class MainViewModel(private  val gratitudeUseCase: GratitudeUseCase) : ViewModel() {
    private val _gratitudeList = MutableStateFlow<List<GratitudeItem>>(emptyList())
    val gratitudeList: StateFlow<List<GratitudeItem>> = _gratitudeList.asStateFlow()
    private val _streak = MutableStateFlow(0)
    val streak: StateFlow<Int> = _streak.asStateFlow()

    val groupedGratitudes: StateFlow<Map<String, List<GratitudeItem>>> = gratitudeList.map{ list ->
            list.groupBy { it.date }
        }
        .stateIn(
            scope = viewModelScope,
            started = SharingStarted.WhileSubscribed(5000),
            initialValue = emptyMap()
        )

    fun saveGratitude(text: String) {
        val item = GratitudeItem(gratitudeText = text)
        viewModelScope.launch {
            gratitudeUseCase.execute(item)
            _gratitudeList.value = _gratitudeList.value + item
            loadGratitudes()
        }
    }

    fun updateGratitude(item: GratitudeItem) {
        viewModelScope.launch {
            gratitudeUseCase.update(item)
            loadGratitudes()
        }
    }

    fun deleteGratitude(item: GratitudeItem) {
        viewModelScope.launch {
            gratitudeUseCase.delete(item)
            loadGratitudes()
        }
    }

    fun loadGratitudes() {
        viewModelScope.launch {
            val gratitudeList = gratitudeUseCase.getAllGratitudes() // 모든 감사한 일 로드
            _gratitudeList.value = gratitudeList

            // 연속기록 계산 및 업데이트
            val currentStreak = calculateStreak(gratitudeList)
            _streak.value = currentStreak
        }
    }

    fun deleteAllGratitudes() {
        viewModelScope.launch {
            gratitudeUseCase.deleteAllGratitude()
            loadGratitudes()
        }
    }

    private fun calculateStreak(gratitudes: List<GratitudeItem>): Int {
        if (gratitudes.isEmpty()) return 0

        val dateFormat = SimpleDateFormat("yyyy-MM-dd", Locale.getDefault())

        val dates = gratitudes.map { it.date }
            .distinct()
            .sortedDescending()

        val todayStr = dateFormat.format(Date())
        if (dates[0] != todayStr) {
            // 오늘 기록이 없으면 연속0일
            return 0
        }

        var streak = 1
        var prevDate = dateFormat.parse(dates[0])!!

        for (i in 1 until dates.size) {
            val currentDate = dateFormat.parse(dates[i])!!
            val diff = (prevDate.time - currentDate.time) / (1000 * 60 * 24)

            if (diff == 1L) {
                streak++
                prevDate = currentDate
            } else if (diff > 1L) {
                break   // 연속 실패
            } else {
                // 같은 날짜 중 시 무시
                prevDate = currentDate
            }
        }

        return streak
    }
}