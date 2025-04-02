package com.dhkim.gamsahanilsang.presentation.viewmodel

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.dhkim.gamsahanilsang.domain.entity.GratitudeItem
import com.dhkim.gamsahanilsang.domain.usecase.SaveGratitudeUseCase
import kotlinx.coroutines.launch

class MainViewModel(private  val saveGratitudeUseCase: SaveGratitudeUseCase) : ViewModel() {
    private val _gratitudeList = MutableLiveData<List<GratitudeItem>>()
    val gratitudeList: LiveData<List<GratitudeItem>> get() = _gratitudeList

    fun saveGratitude(text: String) {
        val item = GratitudeItem(gratitudeText = text)
        viewModelScope.launch {
            saveGratitudeUseCase.execute(item)
            val currentList = _gratitudeList.value ?: emptyList()
            _gratitudeList.value = currentList + item
        }
    }

    fun updateGratitude(item: GratitudeItem) {
        viewModelScope.launch {
            saveGratitudeUseCase.update(item)
            loadGratitudes()
        }
    }

    fun loadGratitudes() {
        viewModelScope.launch {
            val gratitudeList = saveGratitudeUseCase.getAllGratitudes() // 모든 감사한 일 로드
            _gratitudeList.value = gratitudeList
        }
    }

    fun deleteAllGratitudes() {
        viewModelScope.launch {
            saveGratitudeUseCase.deleteAllGratitude()
        }
    }
}