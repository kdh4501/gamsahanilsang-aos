package com.dhkim.gamsahanilsang.domain.usecase

import com.dhkim.gamsahanilsang.domain.entity.GratitudeItem
import com.dhkim.gamsahanilsang.domain.model.GratitudeFilter
import com.dhkim.gamsahanilsang.domain.repository.GratitudeRepository
import kotlinx.coroutines.flow.Flow
import javax.inject.Inject

class GetFilteredGratitudeItemUseCase @Inject constructor(
    private val gratitudeRepository: GratitudeRepository
) {
    suspend operator fun invoke(filter: GratitudeFilter): Flow<List<GratitudeItem>> {
        return gratitudeRepository.getFilteredGratitudeItems(filter)
    }
}