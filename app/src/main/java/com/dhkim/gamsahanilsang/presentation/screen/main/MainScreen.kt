package com.dhkim.gamsahanilsang.presentation.screen.main

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.Search
import androidx.compose.material.icons.filled.Settings
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.CenterAlignedTopAppBar
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Divider
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.FloatingActionButton
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import com.dhkim.gamsahanilsang.domain.model.GratitudeEntry
import com.dhkim.gamsahanilsang.presentation.navigation.AppDestinations
import com.dhkim.gamsahanilsang.presentation.viewModel.GratitudeListViewModel
import java.text.SimpleDateFormat
import java.util.Locale

// 필요한 경우 SearchBar 임포트 (검색 기능 구현 시 사용)
// import com.dhkim.gamsahanilsang.presentation.ui.GratitudeSearchBar


/**
 * 감사 기록 목록 화면 Composable.
 * ViewModel의 상태를 보고 로딩, 에러, 빈 목록, 데이터 목록 UI를 표시합니다.
 */
@Composable
fun MainScreen(
    navController: NavController, // 네비게이션 컨트롤러
    viewModel: GratitudeListViewModel // ViewModel
) {
    // ViewModel의 UI 상태 관찰
    val uiState by viewModel.uiState.collectAsState()

    // 상세/편집 다이얼로그 상태 관리
    var showEditDialog by remember { mutableStateOf(false) }
    var selectedEntry by remember { mutableStateOf<GratitudeEntry?>(null) }

    Scaffold(
        topBar = {
            // 상단 앱 바 (검색 및 설정 버튼)
            MainAppTopBar(
                onSearchClick = {
                    // TODO: 검색 기능 활성화 또는 검색 화면 이동
                    println("검색 버튼 클릭!")
                },
                onSettingsClick = {
                    // 설정 화면 이동
                    navController.navigate(AppDestinations.SETTINGS_ROUTE)
                }
            )
        },
        floatingActionButton = {
            // 새 기록 추가 버튼
            FloatingActionButton(onClick = {
                // TODO: 새 기록 추가 화면 이동
                // navController.navigate(AppDestinations.ADD_GRATITUDE_ROUTE)
                println("새 기록 추가 버튼 클릭! -> 추가 화면 이동 필요")
            }) {
                Icon(Icons.Default.Add, contentDescription = "새 기록 추가")
            }
        }
    ) { paddingValues -> // Scaffold 패딩
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues)
                .background(MaterialTheme.colorScheme.background)
                .padding(horizontal = 16.dp)
        ) {
            // 화면 제목
            Text(
                text = "나의 감사 기록",
                style = MaterialTheme.typography.headlineMedium,
                modifier = Modifier.padding(vertical = 16.dp)
            )

            // ViewModel 상태에 따른 조건부 UI 표시
            when {
                // 로딩 중
                uiState.isLoading -> {
                    Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                        CircularProgressIndicator()
                    }
                }
                // 에러 발생
                uiState.error != null -> {
                    Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                        Text(text = "오류: ${uiState.error}", color = MaterialTheme.colorScheme.error)
                    }
                }
                // 데이터 없음 (로딩 중 아닐 때)
                uiState.entries.isEmpty() && !uiState.isLoading -> {
                    Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                        Text("아직 기록된 감사한 일이 없어요!\n오늘 감사한 일을 기록해보세요!")
                    }
                }
                // 데이터 있음
                else -> {
                    // 👇 실제 감사 기록 목록 표시 - GratitudeList Composable 사용
                    GratitudeList(
                        entries = uiState.entries, // ViewModel에서 가져온 데이터 전달
                        onEntryClick = { entry ->
                            // 항목 클릭 시 다이얼로그 표시 상태 업데이트
                            selectedEntry = entry
                            showEditDialog = true
                            println("기록 클릭: ${entry.id}")
                        },
                        onDeleteClick = { entry ->
                            // 삭제 요청 시 ViewModel 함수 호출
                            viewModel.deleteGratitudeEntry(entry.id)
                            println("기록 삭제 요청: ${entry.id}")
                        }
                        // TODO: onEditClick 등 필요한 콜백 전달
                    )
                }
            }
        }
    }

    // 상세/편집 다이얼로그 표시 (selectedEntry와 showEditDialog 상태에 따라)
    if (showEditDialog && selectedEntry != null) {
        EditGratitudeDialog(
            entry = selectedEntry!!, // 선택된 기록 전달
            onSave = { updatedEntry ->
                // 다이얼로그에서 저장 시 ViewModel 함수 호출
                viewModel.updateGratitudeEntry(updatedEntry)
                showEditDialog = false
                selectedEntry = null // 상태 초기화
            },
            onDelete = { entryToDelete ->
                // 다이얼로그에서 삭제 시 ViewModel 함수 호출
                viewModel.deleteGratitudeEntry(entryToDelete.id)
                showEditDialog = false
                selectedEntry = null // 상태 초기화
            },
            onDismiss = {
                // 다이얼로그 닫기 요청 처리
                showEditDialog = false
                selectedEntry = null // 상태 초기화
            }
        )
    }
}

// --- MainScreen에서 사용될 하위 Composable 함수들 ---

/**
 * 기본적인 메인 화면 상단 앱 바.
 */
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun MainAppTopBar(onSearchClick: () -> Unit, onSettingsClick: () -> Unit) {
    CenterAlignedTopAppBar(
        title = { Text("감사한 일상") },
        actions = {
            IconButton(onClick = onSearchClick) {
                Icon(Icons.Default.Search, contentDescription = "검색")
            }
            IconButton(onClick = onSettingsClick) {
                Icon(Icons.Default.Settings, contentDescription = "설정")
            }
        }
    )
}

/**
 * 감사 기록 목록을 표시하는 Composable 함수 (LazyColumn 사용).
 * 이 Composable은 데이터를 받아와서 UI만 그립니다.
 */
@Composable
fun GratitudeList(
    entries: List<GratitudeEntry>, // 표시할 데이터 리스트 (GratitudeEntry 타입)
    onEntryClick: (GratitudeEntry) -> Unit,
    onEditClick: ((GratitudeEntry) -> Unit)? = null,
    onDeleteClick: ((GratitudeEntry) -> Unit)? = null
) {
    LazyColumn(
        modifier = Modifier.fillMaxSize(),
        contentPadding = PaddingValues(vertical = 8.dp),
        verticalArrangement = Arrangement.spacedBy(8.dp)
    ) {
        // GratitudeEntry 리스트를 기반으로 항목 생성
        items(entries, key = { it.id }) { entry -> // key는 GratitudeEntry의 id (String) 사용
            GratitudeItem( // 개별 항목 UI Composable
                entry = entry, // GratitudeEntry 객체 전달
                onClick = { onEntryClick(entry) },
                onDeleteClick = { onDeleteClick?.invoke(entry) } // null 체크 후 호출
                // TODO: onEditClick 전달 (필요시)
            )
            Divider()
        }
    }
}

/**
 * 개별 감사 기록 항목 UI.
 */
@Composable
fun GratitudeItem(
    entry: GratitudeEntry, // GratitudeEntry 객체를 받음
    onClick: () -> Unit,
    onDeleteClick: () -> Unit // 삭제 콜백
    // TODO: onEditClick 콜백 (필요시)
) {
    Surface(
        modifier = Modifier
            .fillMaxWidth()
            .clickable(onClick = onClick), // 항목 클릭
        color = MaterialTheme.colorScheme.surfaceVariant
    ) {
        Row(
            modifier = Modifier
                .padding(16.dp)
                .fillMaxWidth(),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            Column(modifier = Modifier.weight(1f).padding(end = 8.dp)) {
                // GratitudeEntry 필드 사용
                if (entry.title.isNotEmpty()) {
                    Text(text = entry.title, style = MaterialTheme.typography.titleMedium, maxLines = 1)
                }
                // GratitudeEntry 필드 사용
                Text(text = entry.content, style = MaterialTheme.typography.bodyMedium, maxLines = if (entry.title.isNotEmpty()) 2 else 3)
                // GratitudeEntry 필드 사용 (Date 타입)
                entry.timestamp?.let {
                    Text(
                        text = SimpleDateFormat("yyyy.MM.dd HH:mm", Locale.getDefault()).format(it),
                        style = MaterialTheme.typography.bodySmall,
                        color = MaterialTheme.colorScheme.onSurfaceVariant,
                        modifier = Modifier.padding(top = 4.dp)
                    )
                }
            }
            // 삭제 버튼
            IconButton(onClick = onDeleteClick) {
                Icon(
                    imageVector = Icons.Filled.Delete,
                    contentDescription = "삭제",
                    tint = MaterialTheme.colorScheme.error
                )
            }
            // TODO: 편집 버튼 추가 (필요시)
        }
    }
}

/**
 * 감사 기록 편집/상세 정보를 표시하는 다이얼로그.
 */
@Composable
fun EditGratitudeDialog(
    entry: GratitudeEntry, // GratitudeEntry 객체 받음
    onSave: (GratitudeEntry) -> Unit,
    onDelete: (GratitudeEntry) -> Unit,
    onDismiss: () -> Unit
) {
    // TODO: 실제 편집 가능한 UI (TextField 등) 구현 필요
    // 현재는 데이터 표시 및 버튼만 있습니다.
    AlertDialog(
        onDismissRequest = onDismiss,
        title = { Text("기록 상세/편집") },
        text = {
            Column {
                Text("ID: ${entry.id}")
                Text("사용자 ID: ${entry.userId}")
                Text("내용: ${entry.content}")
                // TODO: 편집 TextField 추가 및 상태 관리
                // var editedContent by remember { mutableStateOf(entry.content) }
                // TextField(value = editedContent, onValueChange = { editedContent = it })
            }
        },
        confirmButton = {
            // TODO: 편집된 내용을 반영하여 updatedEntry 생성 후 onSave 호출
            Button(onClick = {
                // 임시: 변경 없이 onSave 호출 (실제 편집 로직 추가 필요)
                // val updatedEntry = entry.copy(content = editedContent) // 예시
                onSave(entry) // 임시로 원본 객체 전달
            }) {
                Text("저장")
            }
        },
        dismissButton = {
            Row {
                TextButton(onClick = { onDelete(entry) }) {
                    Text("삭제", color = MaterialTheme.colorScheme.error)
                }
                TextButton(onClick = onDismiss) {
                    Text("취소")
                }
            }
        }
    )
}

// TODO: 검색창 Composable (GratitudeSearchBar)도 여기에 포함하거나 별도 파일로 분리 가능
// @Composable fun GratitudeSearchBar(...) { ... }
