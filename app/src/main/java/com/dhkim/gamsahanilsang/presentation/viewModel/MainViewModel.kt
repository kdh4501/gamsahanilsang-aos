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

class MainViewModel(private  val gratitudeUseCase: GratitudeUseCase) : ViewModel() {
    private val _gratitudeList = MutableStateFlow<List<GratitudeItem>>(emptyList())
    val gratitudeList: StateFlow<List<GratitudeItem>> = _gratitudeList.asStateFlow()

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
        }
    }

    fun deleteAllGratitudes() {
        viewModelScope.launch {
            gratitudeUseCase.deleteAllGratitude()
            loadGratitudes()
        }
    }
}