package com.dhkim.gamsahanilsang.presentation.activity.main

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.dhkim.gamsahanilsang.domain.usecase.SaveGratitudeUseCase

class MainViewModelFactory(private val saveGratitudeUseCase: SaveGratitudeUseCase) : ViewModelProvider.Factory {

    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        if (modelClass.isAssignableFrom(MainViewModel::class.java)) {
            @Suppress("UNCHECKED_CAST")
            return MainViewModel(saveGratitudeUseCase) as T
        }
        // 타입이 맞지 않으면 예외 발생
        throw IllegalArgumentException("Unknown ViewModel class")
    }

}