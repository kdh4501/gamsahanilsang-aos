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
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale
import kotlin.math.abs

class MainViewModel(private  val gratitudeUseCase: GratitudeUseCase) : ViewModel() {
    companion object {
        private const val DATE_FORMAT = "yyyy-MM-dd"
        private const val FLOW_TIMEOUT_MS = 5000L
    }

    // UI State
    private val _uiState = MutableStateFlow(MainUiState())
    val uiState = _uiState.asStateFlow()

    val groupedGratitudes: StateFlow<Map<String, List<GratitudeItem>>> = _uiState.map{
        it.gratitudeList.groupBy { item -> item.date }
    }.stateIn(
        scope = viewModelScope,
        started = SharingStarted.WhileSubscribed(FLOW_TIMEOUT_MS),
        initialValue = emptyMap()
    )

    init {
        loadGratitudes()
    }

    fun markStreakToastShown() {
        _uiState.update { it.copy(isStreakToastShown = true) }
    }

    fun saveGratitude(text: String) {
        if (text.isBlank()) return

        val item = GratitudeItem(gratitudeText = text)
        viewModelScope.launch {
            try {
                gratitudeUseCase.execute(item)
                loadGratitudes()
            } catch (e: Exception) {
                _uiState.update { it.copy(error = e.message) }
            }
        }
    }

    fun updateGratitude(item: GratitudeItem) {
        viewModelScope.launch {
            try {
                gratitudeUseCase.update(item)
                loadGratitudes()
            } catch (e: Exception) {
                _uiState.update { it.copy(error = e.message) }
            }
        }
    }

    fun deleteGratitude(item: GratitudeItem) {
        viewModelScope.launch {
            try {
                gratitudeUseCase.delete(item)
                loadGratitudes()
            } catch (e: Exception) {
                _uiState.update { it.copy(error = e.message) }
            }

        }
    }

    fun loadGratitudes() {
        viewModelScope.launch {
            try {
                val gratitudeList = gratitudeUseCase.getAllGratitudes() // 모든 감사한 일 로드
                val currentStreak = calculateStreak(gratitudeList)

                _uiState.update {
                    it.copy(
                        gratitudeList = gratitudeList,
                        streak = currentStreak,
                        isLoading = false,
                        error = null
                    )
                }
            } catch (e: Exception) {
                _uiState.update {
                    it.copy(
                        isLoading = false,
                        error = e.message
                    )
                }
            }
        }
    }

    fun deleteAllGratitudes() {
        viewModelScope.launch {
            try {
                gratitudeUseCase.deleteAllGratitude()
                loadGratitudes()
            } catch (e: Exception) {
                _uiState.update { it.copy(error = e.message) }
            }
        }
    }

    private fun calculateStreak(gratitudes: List<GratitudeItem>): Int {
        if (gratitudes.isEmpty()) return 0

        val dateFormat = SimpleDateFormat(DATE_FORMAT, Locale.getDefault())

        // 날짜별로 그룹화하고 정렬
        val dates = gratitudes.map { it.date }
            .distinct()
            .sortedDescending()

        // 오늘 날짜와 비교
        val todayStr = dateFormat.format(Date())
        if (dates.isEmpty() || dates[0] != todayStr) {
            return 0    // 오늘 기록이 없으면 연속0일
        }

        var streak = 1
        var prevDate = dateFormat.parse(dates[0])!!

        for (i in 1 until dates.size) {
            val nextDate = dateFormat.parse(dates[i])!!

            val diffInMillis = prevDate.time - nextDate.time
            val diffInDays = abs(diffInMillis) / (1000 * 60 * 60 * 24)

            if (diffInDays == 1L) {
                streak++
                prevDate = nextDate
            } else if (diffInDays > 1L) {
                break   // 연속 실패
            }
        }

        return streak
    }

    // UI 상태를 위한 데이터 클래스
    data class MainUiState(
        val gratitudeList: List<GratitudeItem> = emptyList(),
        val streak: Int = 0,
        val isStreakToastShown: Boolean = false,
        val isLoading: Boolean = true,
        val error: String? = null
    )
}