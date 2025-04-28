package com.dhkim.gamsahanilsang.presentation.viewModel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.dhkim.gamsahanilsang.domain.entity.GratitudeItem
import com.dhkim.gamsahanilsang.domain.usecase.GratitudeUseCase
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import java.text.SimpleDateFormat
import java.util.Calendar
import java.util.Locale
import javax.inject.Inject

@HiltViewModel
class MainViewModel @Inject constructor(
    private  val gratitudeUseCase: GratitudeUseCase
) : ViewModel() {
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
        val datesSet = gratitudes.map { it.date }

        // 날짜 문자열을 Date 객체로 변환하고 내림차순 정렬
        val dateObjects = datesSet.mapNotNull {
            try { dateFormat.parse(it) } catch (e: Exception) { null }
        }.sortedByDescending { it.time }

        if (dateObjects.isEmpty()) return 0

        // 오늘 날짜와 비교
        val today = Calendar.getInstance().apply {
            set(Calendar.HOUR_OF_DAY, 0)
            set(Calendar.MINUTE, 0)
            set(Calendar.SECOND, 0)
            set(Calendar.MILLISECOND, 0)
        }.time

        // 가장 최근 기록이 오늘이 아니면 연속 0일
        val mostRecentDate = Calendar.getInstance().apply {
            time = dateObjects[0]
            set(Calendar.HOUR_OF_DAY, 0)
            set(Calendar.MINUTE, 0)
            set(Calendar.SECOND, 0)
            set(Calendar.MILLISECOND, 0)
        }.time

        if (mostRecentDate.time != today.time) return 0

        var streak = 1
        var prevCalendar = Calendar.getInstance().apply { time = dateObjects[0] }

        for (i in 1 until dateObjects.size) {
            val currentCalendar = Calendar.getInstance().apply { time = dateObjects[i] }

            // 이전 날짜에서 하루 빼기
            prevCalendar.add(Calendar.DAY_OF_YEAR, -1)

            // 날짜만 비교 (시간 무시)
            val prevDateOnly = Calendar.getInstance().apply {
                set(prevCalendar.get(Calendar.YEAR), prevCalendar.get(Calendar.MONTH), prevCalendar.get(Calendar.DAY_OF_MONTH), 0, 0, 0)
                set(Calendar.MILLISECOND, 0)
            }

            val currDateOnly = Calendar.getInstance().apply {
                set(currentCalendar.get(Calendar.YEAR), currentCalendar.get(Calendar.MONTH), currentCalendar.get(Calendar.DAY_OF_MONTH), 0, 0, 0)
                set(Calendar.MILLISECOND, 0)
            }

            if (prevDateOnly.time.time == currDateOnly.time.time) {
                // 연속된 날짜 발견
                streak++
                prevCalendar = currentCalendar
            } else {
                // 연속 중단
                break
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