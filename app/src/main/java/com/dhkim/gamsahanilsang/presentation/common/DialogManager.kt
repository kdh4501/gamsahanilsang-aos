package com.dhkim.gamsahanilsang.presentation.common

import android.content.Context
import com.dhkim.gamsahanilsang.domain.entity.GratitudeItem
import com.dhkim.gamsahanilsang.domain.model.GratitudeFilter
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow

/**
 * Compose 환경에서 사용할 수 있는 다이얼로그 관리자
 */
class DialogManager(private val context: Context) {
    // 현재 표시 중인 다이얼로그 상태
    private val _currentDialog = MutableStateFlow<DialogType?>(null)
    val currentDialog = _currentDialog.asStateFlow()

    // 다이얼로그 타입 정의
    sealed class DialogType {
        data class EditDialog(
            val gratitudeItem: GratitudeItem,
            val onSave: (GratitudeItem) -> Unit,
            val onDelete: (GratitudeItem) -> Unit,
            val onDismiss: () -> Unit
        ) : DialogType()

        // 필터 다이얼로그
        data class FilterDialog(
            val currentFilter: GratitudeFilter,
            val onFilterApplied: (GratitudeFilter) -> Unit,
            val onFilterReset: () -> Unit,
            val onDismiss: () -> Unit
        ) : DialogType()
    }

    fun showEditDialog(
        gratitudeItem: GratitudeItem,
        onSave: (GratitudeItem) -> Unit,
        onDelete: (GratitudeItem) -> Unit
    ) {
        _currentDialog.value = DialogType.EditDialog(
            gratitudeItem = gratitudeItem,
            onSave = { item ->
                onSave(item)
                hideDialog()
            },
            onDelete = { item ->
                onDelete(item)
                hideDialog()
            },
            onDismiss = { hideDialog() }
        )
    }

    // 필터 다이얼로그 표시
    fun showFilterDialog(
        currentFilter: GratitudeFilter,
        onFilterApplied: (GratitudeFilter) -> Unit,
        onFilterReset: () -> Unit
    ) {
        _currentDialog.value = DialogType.FilterDialog(
            currentFilter = currentFilter,
            onFilterApplied = onFilterApplied,
            onFilterReset = onFilterReset,
            onDismiss = { hideDialog() }
        )
    }

    // 다이얼로그 숨기기
    fun hideDialog() {
        _currentDialog.value = null
    }
}