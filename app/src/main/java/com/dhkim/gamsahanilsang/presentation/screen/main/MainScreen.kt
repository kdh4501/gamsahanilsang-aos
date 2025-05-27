package com.dhkim.gamsahanilsang.presentation.screen.main

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.FloatingActionButton
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
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

@Composable
fun MainScreen(
    navController: NavController, // NavController를 인자로 받습니다.
    viewModel: GratitudeListViewModel // ViewModel을 인자로 받습니다.
) {
    // ViewModel의 UI 상태(uiState)를 관찰합니다. 상태가 바뀔 때마다 이 Composable이 다시 그려집니다.
    val uiState by viewModel.uiState.collectAsState()

    // TODO: 상세/편집 다이얼로그 상태 관리 (ViewModel에서 관리하거나 여기서 상태로 관리)
    var showEditDialog by remember { mutableStateOf(false) }
    var selectedEntry by remember { mutableStateOf<GratitudeEntry?>(null) }

    // Scaffold는 기본적인 Material Design 레이아웃 구조(AppBar, FAB 등)를 제공합니다.
    Scaffold(
        // 상단 앱 바 정의
        topBar = {
            // TODO: 검색 UI 활성화 상태에 따라 다른 TopBar 표시 로직 (MainViewModel과 연동)
            // 현재는 간단한 기본 TopBar를 보여줍니다. 검색 기능 구현 시 이 부분을 수정해야 합니다.
            MainAppTopBar(
                onSearchClick = {
                    // TODO: 검색 기능 활성화 또는 검색 화면으로 이동
                    println("검색 버튼 클릭!")
                },
                onSettingsClick = {
                    // 설정 화면으로 이동
                    navController.navigate(AppDestinations.SETTINGS_ROUTE) // 설정 화면 경로로 이동
                }
            )
        },
        // 하단에 떠 있는 추가 버튼 정의 (FAB)
        floatingActionButton = {
            FloatingActionButton(onClick = {
                // 새 감사 기록 추가 화면으로 이동 (TODO: 경로 추가 및 네비게이션)
                // navController.navigate(AppDestinations.ADD_GRATITUDE_ROUTE)
                println("새 기록 추가 버튼 클릭! -> 추가 화면 이동 필요")
            }) {
                Icon(Icons.Default.Add, contentDescription = "새 기록 추가")
            }
        }
    ) { paddingValues -> // Scaffold가 제공하는 패딩 값을 받아서 내용물에 적용해야 합니다.
        Column(
            modifier = Modifier
                .fillMaxSize() // 전체 화면 채우기
                .padding(paddingValues) // Scaffold 패딩 적용 (필수!)
                .background(MaterialTheme.colorScheme.background) // 배경색 설정
                .padding(horizontal = 16.dp) // 좌우 여백
        ) {
            // 화면 제목
            Text(
                text = "나의 감사 기록",
                style = MaterialTheme.typography.headlineMedium,
                modifier = Modifier.padding(vertical = 16.dp) // 상하 여백
            )

            // ViewModel의 uiState에 따라 로딩, 에러, 데이터 목록 표시
            when {
                // 로딩 중 상태이면 로딩 스피너 표시
                uiState.isLoading -> {
                    Box(
                        modifier = Modifier.fillMaxSize(),
                        contentAlignment = Alignment.Center
                    ) {
                        CircularProgressIndicator()
                    }
                }
                // 에러 상태이면 에러 메시지 표시
                uiState.error != null -> {
                    Box(
                        modifier = Modifier.fillMaxSize(),
                        contentAlignment = Alignment.Center
                    ) {
                        Text(
                            text = "오류: ${uiState.error}",
                            color = MaterialTheme.colorScheme.error // 에러 색상 적용
                        )
                    }
                }
                // 로딩도 아니고 에러도 아닌데 기록 목록이 비어있으면 메시지 표시
                uiState.entries.isEmpty() -> {
                    Box(
                        modifier = Modifier.fillMaxSize(),
                        contentAlignment = Alignment.Center
                    ) {
                        Text("아직 기록된 감사한 일이 없어요!")
                    }
                }
                // 위 세 가지 상태가 아니면 실제 감사 기록 목록을 표시
                else -> {
//                    GratitudeList( // 감사 기록 목록 표시를 위한 하위 Composable
//                        entries = uiState.entries, // ViewModel에서 가져온 데이터 전달
//                        onEntryClick = { entry ->
//                            // 기록 클릭 시 처리 (상세 보기 또는 편집 다이얼로그 표시)
//                            selectedEntry = entry
//                            showEditDialog = true // 다이얼로그 표시 상태 변경
//                            println("기록 클릭: ${entry.id}") // 로그
//                        },
//                        onDeleteClick = { entry ->
//                            // 기록 삭제 처리 - ViewModel 함수 호출
//                            viewModel.deleteGratitudeEntry(entry.id)
//                            println("기록 삭제 요청: ${entry.id}") // 로그
//                        }
//                    )
                }
            }
        }
    }

//    // 상세/편집 다이얼로그 표시 로직
//    // selectedEntry와 showEditDialog 상태에 따라 다이얼로그를 조건부로 표시합니다.
//    if (showEditDialog && selectedEntry != null) {
//        EditGratitudeDialog( // TODO: 편집 다이얼로그 Composable 구현 필요
//            entry = selectedEntry!!, // 선택된 기록 전달 (non-null임을 확신)
//            onSave = { updatedEntry ->
//                // 다이얼로그에서 저장 버튼 클릭 시 ViewModel 함수 호출
//                viewModel.updateGratitudeEntry(updatedEntry)
//                showEditDialog = false // 다이얼로그 닫기
//            },
//            onDelete = { entryToDelete ->
//                // 다이얼로그에서 삭제 버튼 클릭 시 ViewModel 함수 호출
//                viewModel.deleteGratitudeEntry(entryToDelete.id)
//                showEditDialog = false // 다이얼로그 닫기
//            },
//            onDismiss = {
//                // 다이얼로그 외부 클릭 또는 취소 버튼 시 다이얼로그 닫기
//                showEditDialog = false
//            }
//        )
//    }
}