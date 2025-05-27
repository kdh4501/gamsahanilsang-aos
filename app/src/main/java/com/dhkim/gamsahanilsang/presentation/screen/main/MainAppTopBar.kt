package com.dhkim.gamsahanilsang.presentation.screen.main

import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Search
import androidx.compose.material.icons.filled.Settings
import androidx.compose.material3.CenterAlignedTopAppBar
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable

/**
 * 기본적인 메인 화면 상단 앱 바를 정의하는 Composable 함수입니다.
 * 검색 및 설정 버튼을 포함합니다.
 *
 * @param onSearchClick 검색 버튼 클릭 시 호출될 콜백
 * @param onSettingsClick 설정 버튼 클릭 시 호출될 콜백
 */
@OptIn(ExperimentalMaterial3Api::class) // CenterAlignedTopAppBar 사용 시 필요
@Composable
fun MainAppTopBar(onSearchClick: () -> Unit, onSettingsClick: () -> Unit) {
    CenterAlignedTopAppBar(
        title = { Text("감사한 일상") }, // 앱 제목
        actions = { // 상단 바 우측 액션 버튼들
            // 검색 버튼
            IconButton(onClick = onSearchClick) {
                Icon(Icons.Default.Search, contentDescription = "검색")
            }
            // 설정 버튼
            IconButton(onClick = onSettingsClick) {
                Icon(Icons.Default.Settings, contentDescription = "설정")
            }
        }
        // navigationIcon 등 필요시 추가
    )
}