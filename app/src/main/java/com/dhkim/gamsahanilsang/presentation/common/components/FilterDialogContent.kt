package com.dhkim.gamsahanilsang.presentation.common.components

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.layout.wrapContentHeight
import androidx.compose.foundation.selection.selectable
import androidx.compose.foundation.selection.selectableGroup
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.DateRange
import androidx.compose.material.icons.filled.Search
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Divider
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.OutlinedTextFieldDefaults
import androidx.compose.material3.RadioButton
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.semantics.Role
import androidx.compose.ui.unit.dp
import androidx.compose.ui.window.Dialog
import com.dhkim.gamsahanilsang.R
import com.dhkim.gamsahanilsang.domain.model.DateRange
import com.dhkim.gamsahanilsang.domain.model.GratitudeFilter
import com.dhkim.gamsahanilsang.domain.model.SortOrder
import java.time.LocalDate
import java.time.format.DateTimeFormatter

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun FilterDialogContent(
    currentFilter: GratitudeFilter,
    onFilterApplied: (GratitudeFilter) -> Unit,
    onFilterReset: () -> Unit,
    onDismiss: () -> Unit
) {
    // 필터 상태 관리
    var filter by remember { mutableStateOf(currentFilter) }
    var showDateRangePicker by remember { mutableStateOf(false) }

    Dialog(onDismissRequest = onDismiss) {
        Box(
            modifier = Modifier
                .fillMaxWidth(0.9f)
                .wrapContentHeight()
                .padding(top = 16.dp),
            contentAlignment = Alignment.TopCenter
        ) {
            Surface(
                shape = RoundedCornerShape(16.dp),
                color = MaterialTheme.colorScheme.surfaceVariant,
                tonalElevation = 8.dp,
                shadowElevation = 8.dp
            ) {
                Column(
                    modifier = Modifier.padding(20.dp),
                    verticalArrangement = Arrangement.spacedBy(16.dp)
                ) {
                    // 제목
                    Text(
                        text = stringResource(R.string.filter_title),
                        style = MaterialTheme.typography.titleLarge,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )

                    Divider(color = MaterialTheme.colorScheme.outlineVariant)

                    // 날짜 범위 선택
                    Column {
                        Text(
                            text = stringResource(R.string.date_range),
                            style = MaterialTheme.typography.titleMedium,
                            color = MaterialTheme.colorScheme.onSurfaceVariant
                        )

                        Row(
                            modifier = Modifier
                                .fillMaxWidth()
                                .clickable { showDateRangePicker = true }
                                .padding(vertical = 8.dp),
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Icon(
                                imageVector = Icons.Default.DateRange,
                                contentDescription = stringResource(R.string.date_range),
                                tint = MaterialTheme.colorScheme.primary
                            )

                            Spacer(modifier = Modifier.width(8.dp))

                            Text(
                                text = if (filter.dateRange != null) {
                                    "${filter.dateRange?.startDate?.format(DateTimeFormatter.ISO_LOCAL_DATE)} ~ " +
                                            "${filter.dateRange?.endDate?.format(DateTimeFormatter.ISO_LOCAL_DATE)}"
                                } else {
                                    stringResource(R.string.select_date_range)
                                },
                                style = MaterialTheme.typography.bodyLarge,
                                color = MaterialTheme.colorScheme.onSurfaceVariant
                            )
                        }
                    }

                    // 키워드 입력
                    Column {
                        Text(
                            text = stringResource(R.string.keyword),
                            style = MaterialTheme.typography.titleMedium,
                            color = MaterialTheme.colorScheme.onSurfaceVariant
                        )

                        OutlinedTextField(
                            value = filter.keyword ?: "",
                            onValueChange = {
                                filter = filter.copy(keyword = it.takeIf { it.isNotBlank() })
                            },
                            modifier = Modifier.fillMaxWidth(),
                            colors = OutlinedTextFieldDefaults.colors(
                                focusedBorderColor = MaterialTheme.colorScheme.primary,
                                unfocusedBorderColor = MaterialTheme.colorScheme.outline
                            ),
                            leadingIcon = {
                                Icon(
                                    imageVector = Icons.Default.Search,
                                    contentDescription = stringResource(R.string.keyword)
                                )
                            },
                            singleLine = true,
                            shape = RoundedCornerShape(8.dp)
                        )
                    }

                    // 정렬 순서 선택
                    Column {
                        Text(
                            text = stringResource(R.string.sort_order),
                            style = MaterialTheme.typography.titleMedium,
                            color = MaterialTheme.colorScheme.onSurfaceVariant
                        )

                        Column(modifier = Modifier.selectableGroup()) {
                            SortOrder.values().forEach { order ->
                                Row(
                                    modifier = Modifier
                                        .fillMaxWidth()
                                        .height(48.dp)
                                        .selectable(
                                            selected = filter.sortOrder == order,
                                            onClick = { filter = filter.copy(sortOrder = order) },
                                            role = Role.RadioButton
                                        )
                                        .padding(horizontal = 8.dp),
                                    verticalAlignment = Alignment.CenterVertically
                                ) {
                                    RadioButton(
                                        selected = filter.sortOrder == order,
                                        onClick = null
                                    )

                                    Spacer(modifier = Modifier.width(8.dp))

                                    Text(
                                        text = when (order) {
                                            SortOrder.NEWEST_FIRST -> stringResource(R.string.newest_first)
                                            SortOrder.OLDEST_FIRST -> stringResource(R.string.oldest_first)
                                            SortOrder.ALPHABETICAL -> stringResource(R.string.alphabetical)
                                        }
                                    )
                                }
                            }
                        }
                    }

                    Spacer(modifier = Modifier.height(16.dp))

                    // 버튼 영역
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.End
                    ) {
                        TextButton(
                            onClick = onFilterReset,
                            colors = ButtonDefaults.textButtonColors(
                                contentColor = MaterialTheme.colorScheme.primary
                            )
                        ) {
                            Text(text = stringResource(R.string.reset))
                        }

                        Spacer(modifier = Modifier.width(8.dp))

                        TextButton(
                            onClick = onDismiss,
                            colors = ButtonDefaults.textButtonColors(
                                contentColor = MaterialTheme.colorScheme.onSurfaceVariant
                            )
                        ) {
                            Text(text = stringResource(R.string.cancel))
                        }

                        Spacer(modifier = Modifier.width(8.dp))

                        Button(
                            onClick = { onFilterApplied(filter) },
                            colors = ButtonDefaults.buttonColors(
                                containerColor = MaterialTheme.colorScheme.primary
                            )
                        ) {
                            Text(text = stringResource(R.string.apply))
                        }
                    }
                }
            }
        }
    }

    // 날짜 범위 선택기 표시
    if (showDateRangePicker) {
        DateRangePickerDialog(
            initialStartDate = filter.dateRange?.startDate ?: LocalDate.now().minusMonths(1),
            initialEndDate = filter.dateRange?.endDate ?: LocalDate.now(),
            onDismiss = { showDateRangePicker = false },
            onDateRangeSelected = { startDate, endDate ->
                filter = filter.copy(dateRange = DateRange(startDate, endDate))
                showDateRangePicker = false
            }
        )
    }
}

// 날짜 범위 선택 다이얼로그 컴포넌트
@Composable
fun DateRangePickerDialog(
    initialStartDate: LocalDate,
    initialEndDate: LocalDate,
    onDismiss: () -> Unit,
    onDateRangeSelected: (LocalDate, LocalDate) -> Unit
) {
    // Material3의 DateRangePicker가 아직 안정화되지 않았으므로 커스텀 구현
    // 여기서는 간단한 구현만 제공

    var startDate by remember { mutableStateOf(initialStartDate) }
    var endDate by remember { mutableStateOf(initialEndDate) }

    Dialog(onDismissRequest = onDismiss) {
        Surface(
            shape = RoundedCornerShape(16.dp),
            color = MaterialTheme.colorScheme.surface
        ) {
            Column(
                modifier = Modifier.padding(16.dp),
                verticalArrangement = Arrangement.spacedBy(16.dp)
            ) {
                Text(
                    text = stringResource(R.string.select_date_range),
                    style = MaterialTheme.typography.titleLarge
                )

                // 시작 날짜 선택
                Column {
                    Text(
                        text = stringResource(R.string.start_date),
                        style = MaterialTheme.typography.bodyMedium
                    )

                    // 실제 앱에서는 DatePicker 컴포넌트를 사용하세요
                    // 여기서는 간단한 텍스트 필드로 대체
                    OutlinedTextField(
                        value = startDate.format(DateTimeFormatter.ISO_LOCAL_DATE),
                        onValueChange = {
                            try {
                                startDate = LocalDate.parse(it, DateTimeFormatter.ISO_LOCAL_DATE)
                            } catch (e: Exception) {
                                // 날짜 형식이 잘못된 경우 무시
                            }
                        },
                        modifier = Modifier.fillMaxWidth(),
                        singleLine = true
                    )
                }

                // 종료 날짜 선택
                Column {
                    Text(
                        text = stringResource(R.string.end_date),
                        style = MaterialTheme.typography.bodyMedium
                    )

                    OutlinedTextField(
                        value = endDate.format(DateTimeFormatter.ISO_LOCAL_DATE),
                        onValueChange = {
                            try {
                                endDate = LocalDate.parse(it, DateTimeFormatter.ISO_LOCAL_DATE)
                            } catch (e: Exception) {
                                // 날짜 형식이 잘못된 경우 무시
                            }
                        },
                        modifier = Modifier.fillMaxWidth(),
                        singleLine = true
                    )
                }

                // 버튼 영역
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.End
                ) {
                    TextButton(onClick = onDismiss) {
                        Text(text = stringResource(R.string.cancel))
                    }

                    Spacer(modifier = Modifier.width(8.dp))

                    Button(
                        onClick = {
                            // 시작일이 종료일보다 늦으면 순서를 바꿈
                            if (startDate.isAfter(endDate)) {
                                val temp = startDate
                                startDate = endDate
                                endDate = temp
                            }
                            onDateRangeSelected(startDate, endDate)
                        }
                    ) {
                        Text(text = stringResource(R.string.confirm))
                    }
                }
            }
        }
    }
}

