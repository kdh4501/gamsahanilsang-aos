package com.dhkim.gamsahanilsang.presentation.viewModel

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.map
import androidx.lifecycle.viewModelScope
import com.dhkim.gamsahanilsang.domain.entity.GratitudeItem
import com.dhkim.gamsahanilsang.domain.usecase.SaveGratitudeUseCase
import kotlinx.coroutines.launch

class MainViewModel(private  val saveGratitudeUseCase: SaveGratitudeUseCase) : ViewModel() {
    private val _gratitudeList = MutableLiveData<List<GratitudeItem>>(emptyList())
    val gratitudeList: LiveData<List<GratitudeItem>> = _gratitudeList
    private val _groupedGratitudes = MutableLiveData<Map<String, List<GratitudeItem>>>()
    val groupedGratitudes: LiveData<Map<String, List<GratitudeItem>>> = gratitudeList.map { list ->
            list.groupBy { it.date }
        }


    fun saveGratitude(text: String) {
        val item = GratitudeItem(gratitudeText = text)
        viewModelScope.launch {
            saveGratitudeUseCase.execute(item)
            _gratitudeList.value = _gratitudeList.value?.plus(item) ?: listOf(item)
        }
        loadGratitudes()
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
            loadGratitudes()
        }
    }
}