package com.dhkim.gamsahanilsang.presentation.gratitude.components

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Edit
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.ListItem
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import com.dhkim.gamsahanilsang.domain.entity.GratitudeItem
import com.dhkim.gamsahanilsang.presentation.viewModel.MainViewModel
import com.dhkim.gamsahanilsang.utils.DateUtils

@Composable
fun GratitudeList(
    viewModel: MainViewModel,
    onItemClick: (GratitudeItem) -> Unit,
    onEditClick: ((GratitudeItem) -> Unit)? = null,
    isSearchMode: Boolean = false // 검색 모드 여부
) {
    val groupedItems by viewModel.groupedGratitudes.collectAsState()
    val uiState by viewModel.uiState.collectAsState()

    // 검색 결과 데이터
    val searchResults by viewModel.searchResults.collectAsState()
    val searchQuery by viewModel.searchQuery.collectAsState()

    // 검색 결과를 날짜별로 그룹화
    val groupedSearchResults = searchResults.groupBy { it.date }

    // 검색 모드에 따라 표시할 데이터 결정
    val displayData = if (isSearchMode) groupedSearchResults else groupedItems

    if (uiState.isLoading) {
        Box(
            modifier = Modifier.fillMaxSize(),
            contentAlignment = Alignment.Center
        ) {
            CircularProgressIndicator()
        }
        return
    }

    if (isSearchMode && searchResults.isEmpty() && searchQuery.isNotEmpty()) {
        // 검색 결과가 없는 경우
        Box(
            modifier = Modifier.fillMaxSize(),
            contentAlignment = Alignment.Center
        ) {
            Text(
                text = "\"$searchQuery\"에 대한 검색 결과가 없습니다.",
                style = MaterialTheme.typography.bodyLarge,
                textAlign = TextAlign.Center
            )
        }
    } else if (displayData.isEmpty()) {
        // 데이터가 없는 경우
        Box(
            modifier = Modifier.fillMaxSize(),
            contentAlignment = Alignment.Center
        ) {
            Text(
                text = "아직 감사한 일이 없습니다.\n오늘 감사한 일을 기록해보세요!",
                style = MaterialTheme.typography.bodyLarge,
                textAlign = TextAlign.Center
            )
        }
    } else {
        LazyColumn {
            // 검색 모드일 때 검색 결과 헤더 표시
            if (isSearchMode && searchQuery.isNotEmpty()) {
                item {
                    Text(
                        text = "\"$searchQuery\"에 대한 검색 결과",
                        style = MaterialTheme.typography.titleMedium,
                        modifier = Modifier.padding(bottom = 16.dp)
                    )
                }
            }

            groupedItems.forEach { (date, gratitudeItems) ->
                item {
                    Text(
                        text = formatDateHeader(date),
                        style = MaterialTheme.typography.titleMedium,
                        modifier = Modifier.padding(top = 16.dp, bottom = 8.dp)
                    )
                }
                items(gratitudeItems) { item ->
                    GratitudeListItem(
                        item = item,
                        onClick = { onItemClick(item) },
                        onEditClick = onEditClick
                    )
                }
            }
        }
    }
}

// 날짜 헤더 포맷팅 함수
@Composable
private fun formatDateHeader(dateString: String): String {
    val today = DateUtils.formatToday()
    val yesterday = DateUtils.formatYesterday()

    return when (dateString) {
        today -> "오늘"
        yesterday -> "어제"
        else -> dateString
    }
}

@Composable
fun GratitudeListItem(
    item: GratitudeItem,
    onClick: () -> Unit,
    onEditClick: ((GratitudeItem) -> Unit)?
) {
    ListItem(
        modifier = Modifier
            .fillMaxWidth()
            .clickable(onClick = onClick),
        headlineContent = { Text(item.gratitudeText) },
        trailingContent = {
            if (onEditClick != null) {
                IconButton(onClick = { onEditClick(item) }) {
                    Icon(Icons.Filled.Edit, contentDescription = "Edit")
                }
            }
        }
    )
}