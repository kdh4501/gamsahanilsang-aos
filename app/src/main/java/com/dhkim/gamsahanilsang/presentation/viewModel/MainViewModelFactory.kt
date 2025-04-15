package com.dhkim.gamsahanilsang.presentation.viewModel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.dhkim.gamsahanilsang.domain.usecase.GratitudeUseCase

class MainViewModelFactory(private val gratitudeUseCase: GratitudeUseCase) : ViewModelProvider.Factory {

    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        if (modelClass.isAssignableFrom(MainViewModel::class.java)) {
            @Suppress("UNCHECKED_CAST")
            return MainViewModel(gratitudeUseCase) as T
        }
        // 타입이 맞지 않으면 예외 발생
        throw IllegalArgumentException("Unknown ViewModel class")
    }

}