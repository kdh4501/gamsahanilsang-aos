package com.dhkim.gamsahanilsang.presentation.gratitude

import android.widget.Toast
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Clear
import androidx.compose.material.icons.filled.FilterList
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalSoftwareKeyboardController
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import com.dhkim.gamsahanilsang.R
import com.dhkim.gamsahanilsang.domain.model.GratitudeFilter
import com.dhkim.gamsahanilsang.domain.model.SortOrder
import com.dhkim.gamsahanilsang.presentation.common.DialogManager
import com.dhkim.gamsahanilsang.presentation.common.components.EditDialogContent
import com.dhkim.gamsahanilsang.presentation.common.components.FilterDialogContent
import com.dhkim.gamsahanilsang.presentation.gratitude.components.GratitudeList
import com.dhkim.gamsahanilsang.presentation.viewModel.MainViewModel
import com.dhkim.gamsahanilsang.utils.DateUtils
import java.time.format.DateTimeFormatter

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun GratitudeScreen(
    viewModel: MainViewModel,
    paddingValues: PaddingValues,
    showInputArea: Boolean = false,
    onInputAreaHidden: () -> Unit,
    isSearchActive: Boolean = false
){
    var gratitudeText by remember { mutableStateOf("") }

    val uiState by viewModel.uiState.collectAsState()
    val filterState by viewModel.filterState.collectAsState()
    val streak = uiState.streak
    val isStreakToastShown = uiState.isStreakToastShown

    val keyboardController = LocalSoftwareKeyboardController.current
    val context = LocalContext.current

    // 다이얼로그 관리자
    val dialogManager = remember { DialogManager(context) }

    // 현재 표시 중인 다이얼로 상태 구독
    val currentDialog by dialogManager.currentDialog.collectAsState()

    fun hideKeyboard() {
        keyboardController?.hide()
    }

    val todayStr = remember {
        DateUtils.formatToday()
    }

    LaunchedEffect(key1 = todayStr) {
        if (!isStreakToastShown) {
            Toast.makeText(context,
                context.getString(R.string.streak_toast_message, streak), Toast.LENGTH_SHORT).show()
            viewModel.markStreakToastShown()
        }
    }

    uiState.error?.let { errorMessage ->
        LaunchedEffect(errorMessage) {
            Toast.makeText(context, errorMessage, Toast.LENGTH_SHORT).show()
        }
    }

    Column(
        modifier = Modifier
            .padding(paddingValues)
            .fillMaxSize()
            .background(MaterialTheme.colorScheme.background)
            .padding(16.dp)
    ) {
        // 검색 모드가 아닐 때만 입력 영역 표시
        if (!isSearchActive) {
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(bottom = 8.dp),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                // 필터 버튼
                Button(
                    onClick = {
                        dialogManager.showFilterDialog(
                            currentFilter = filterState,
                            onFilterApplied = { filter ->
                                viewModel.updateFilter(filter)
                            },
                            onFilterReset = {
                                viewModel.resetFilter()
                            }
                        )
                    },
                    shape = RoundedCornerShape(8.dp),
                    border = BorderStroke(1.dp, MaterialTheme.colorScheme.primary),
                    colors = ButtonDefaults.outlinedButtonColors(
                        containerColor = MaterialTheme.colorScheme.primary,
                        contentColor = MaterialTheme.colorScheme.onPrimary
                    )
                ) {
                    Icon(
                        imageVector = Icons.Default.FilterList,
                        contentDescription = stringResource(R.string.filter_title),
                    )
                    Spacer(modifier = Modifier.width(4.dp))
                    Text(
                        text = stringResource(R.string.filter_title),
                    )
                }

                // 활성화된 필터가 있으면 필터 상태 표시
                if (filterState != GratitudeFilter()) {
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        modifier = Modifier
                            .weight(1f)
                            .padding(start = 8.dp)
                    ) {
                        // 필터 상태 요약 표시
                        Text(
                            text = buildFilterSummary(filterState),
                            style = MaterialTheme.typography.bodyMedium,
                            color = MaterialTheme.colorScheme.onSurface,
                            maxLines = 1,
                            overflow = TextOverflow.Ellipsis,
                            modifier = Modifier.weight(1f)
                        )

                        // 필터 초기화 버튼
                        IconButton(onClick = { viewModel.resetFilter() }) {
                            Icon(
                                imageVector = Icons.Default.Clear,
                                contentDescription = stringResource(R.string.filter_reset),
                                tint = MaterialTheme.colorScheme.error
                            )
                        }
                    }
                }
            }

            // 검색 모드가 아닐 때만 입력 영역 표시
            if (showInputArea && !isSearchActive) {
                GratitudeInputArea(
                    gratitudeText = gratitudeText,
                    onTextChange = { gratitudeText = it },
                    onSave = {
                        if (gratitudeText.isNotBlank()) {
                            viewModel.saveGratitude(gratitudeText)
                            gratitudeText = ""
                            hideKeyboard()
                            onInputAreaHidden()
                        }
                    },
                    onClear = { gratitudeText = ""},
                )
            }
        }

        Spacer(modifier = Modifier.height(16.dp))

        // 감사 목록 또는 검색 결과 표시
        GratitudeList(
            viewModel = viewModel,
            onItemClick = { item ->
                // DialogManager를 통해 다이얼로그 표시
                dialogManager.showEditDialog(
                    gratitudeItem = item,
                    onSave = { updatedItem ->
                        viewModel.updateGratitude(updatedItem)
                    },
                    onDelete = { itemToDelete ->
                        viewModel.deleteGratitude(itemToDelete)
                    }
                )
            },
            isSearchMode = isSearchActive // 검색 모드 전달
        )
    }

    // 여기가 FilterDialogContent가 사용되는 부분
    when (val dialog = currentDialog) {
        is DialogManager.DialogType.EditDialog -> {
            EditDialogContent(
                gratitudeItem = dialog.gratitudeItem,
                onSave = dialog.onSave,
                onDelete = dialog.onDelete,
                onDismiss = dialog.onDismiss
            )
        }
        is DialogManager.DialogType.FilterDialog -> {
            // 여기서 FilterDialogContent 컴포넌트를 호출
            FilterDialogContent(
                currentFilter = dialog.currentFilter,
                onFilterApplied = dialog.onFilterApplied,
                onFilterReset = dialog.onFilterReset,
                onDismiss = dialog.onDismiss
            )
        }
        else -> { /* 표시할 다이얼로그 없음 */ }
    }
}

@Composable
fun GratitudeInputArea(
    gratitudeText: String,
    onTextChange: (String) -> Unit,
    onSave: () -> Unit,
    onClear: () -> Unit
) {
    OutlinedTextField(
        value = gratitudeText,
        onValueChange = onTextChange,
        modifier = Modifier
            .fillMaxWidth()
            .padding(10.dp),
        label = { Text(stringResource(R.string.input_hint)) },
        trailingIcon = {
            IconButton(onClick = onClear) {
                Icon(Icons.Filled.Clear, contentDescription = "Clear")
            }
        }
    )

    Spacer(modifier = Modifier.height(8.dp))

    Button(
        onClick = onSave,
        modifier = Modifier.fillMaxWidth()
    ) {
        Text(stringResource(R.string.save_button))
    }
}

// 필터 상태 요약 문자열 생성 함수
@Composable
fun buildFilterSummary(filter: GratitudeFilter): String {
    val parts = mutableListOf<String>()

    // 날짜 범위가 있으면 추가
    filter.dateRange?.let {
        val startDate = it.startDate.format(DateTimeFormatter.ISO_LOCAL_DATE)
        val endDate = it.endDate.format(DateTimeFormatter.ISO_LOCAL_DATE)
        parts.add("$startDate ~ $endDate")
    }

    // 키워드가 있으면 추가
    filter.keyword?.let {
        parts.add("\"$it\"")
    }

    // 정렬 순서 추가
    val sortOrderText = when (filter.sortOrder) {
        SortOrder.NEWEST_FIRST -> stringResource(R.string.newest_first)
        SortOrder.OLDEST_FIRST -> stringResource(R.string.oldest_first)
        SortOrder.ALPHABETICAL -> stringResource(R.string.alphabetical)
    }
    parts.add(sortOrderText)

    return parts.joinToString(", ")
}
