package com.dhkim.gamsahanilsang.presentation.ui.components

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.text.input.rememberTextFieldState
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.Clear
import androidx.compose.material.icons.filled.Search
import androidx.compose.material3.Divider
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.ListItem
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.SearchBar
import androidx.compose.material3.SearchBarDefaults
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.semantics.semantics
import androidx.compose.ui.semantics.traversalIndex
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import com.dhkim.gamsahanilsang.R
import com.dhkim.gamsahanilsang.domain.entity.GratitudeItem
import com.dhkim.gamsahanilsang.utils.DateUtils

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun GratitudeSearchBar(
    query: String,
    onQueryChange: (String) -> Unit,
    onSearch: (String) -> Unit,
    onClearQuery: () -> Unit,
    onBackClick: () -> Unit,
    searchResults: List<GratitudeItem> = emptyList(),
    onResultClick: (GratitudeItem) -> Unit = {},    // 검색 결과 클릭 핸들러 추가
    modifier: Modifier = Modifier
) {
    var expanded by remember { mutableStateOf(false) }
    val textFieldState = rememberTextFieldState(initialText = query)

    // 텍스트 필드 상태와 외부 쿼리 동기화
    LaunchedEffect(query) {
        if (textFieldState.text.toString() != query) {
            textFieldState.edit { replace(0, length, query) }
        }
    }

    LaunchedEffect(textFieldState.text) {
        onQueryChange(textFieldState.text.toString())
    }

    // 검색어가 있으면 자동으로 확장
    LaunchedEffect(query) {
        expanded = query.isNotEmpty()
    }

    SearchBar(
        modifier = modifier
            .fillMaxWidth()
            .semantics { traversalIndex = 0f },
        inputField = {
            SearchBarDefaults.InputField(
                query = textFieldState.text.toString(),
                onQueryChange = { textFieldState.edit { replace(0, length, it) } },
                onSearch = {
                    onSearch(textFieldState.text.toString())
                    // 검색 후에도 확장 상태 유지 (결과 표시를 위해)
                    expanded = textFieldState.text.isNotEmpty()
                },
                expanded = expanded,
                onExpandedChange = { expanded = it },
                placeholder = { Text(stringResource(R.string.search_placeholder_text)) },
                leadingIcon = {
                    Row (verticalAlignment = Alignment.CenterVertically){
                        IconButton(onClick = onBackClick) {
                            Icon(
                                imageVector = Icons.Default.ArrowBack,
                                contentDescription = stringResource(R.string.search_close)
                            )
                        }

                        Icon(
                            imageVector = Icons.Default.Search,
                            contentDescription = stringResource(R.string.search),
                            modifier = Modifier.padding(start = 4.dp)
                        )
                    }
                },
                trailingIcon = {
                    if (textFieldState.text.isNotEmpty()) {
                        IconButton(
                            onClick = {
                                textFieldState.edit { replace(0, length, "") }
                                onClearQuery()
                                expanded = false // 검색어 지우면 축소
                            }
                        ) {
                            Icon(
                                imageVector = Icons.Default.Clear,
                                contentDescription = stringResource(R.string.search_word_clear)
                            )
                        }
                    }
                }
            )
        },
        expanded = expanded,
        onExpandedChange = { expanded = it },
        content = {
            // 검색 결과
            if (query.isNotEmpty()) {
                if (searchResults.isNotEmpty()) {
                    // 검색 결과가 있는 경우
                    LazyColumn {
                        item {
                            Text(
                                text = "\"$query\"에 대한 검색 결과",
                                style = MaterialTheme.typography.titleSmall,
                                modifier = Modifier.padding(horizontal = 16.dp, vertical = 8.dp)
                            )
                        }

                        // 검색 결과를 날짜별로 그룹화
                        val groupedResults = searchResults.groupBy { it.date }

                        groupedResults.forEach { (date, itemsList) ->
                            item {
                                Text(
                                    text = DateUtils.formatDateLabel(date),
                                    style = MaterialTheme.typography.labelLarge,
                                    color = MaterialTheme.colorScheme.primary,
                                    modifier = Modifier.padding(horizontal = 16.dp, vertical = 4.dp)
                                )
                            }

                            itemsList.forEach { item ->
                                item {
                                    ListItem(
                                        headlineContent = {
                                            Text(
                                                text = item.gratitudeText,
                                                maxLines = 2,
                                                overflow = TextOverflow.Ellipsis
                                            )
                                        },
                                        modifier = Modifier
                                            .clickable {
                                                onResultClick(item)
                                                expanded = false // 결과 클릭 시 검색창 축소
                                            }
                                            .fillMaxWidth()
                                            .padding(horizontal = 16.dp)
                                    )
                                    Divider(
                                        modifier = Modifier.padding(horizontal = 16.dp),
                                        thickness = 0.5.dp
                                    )
                                }
                            }
                        }
                    }
                } else {
                    // 검색 결과가 없는 경우
                    Box(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(16.dp),
                        contentAlignment = Alignment.Center
                    ) {
                        Text(
                            text = "\"$query\"에 대한 검색 결과가 없습니다.",
                            style = MaterialTheme.typography.bodyMedium
                        )
                    }
                }
            }
        }
    )
}
