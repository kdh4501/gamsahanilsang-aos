package com.dhkim.gamsahanilsang.presentation.screen.stats

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.tooling.preview.Preview

@Composable
fun StatsScreen() {
    Box(modifier = Modifier.fillMaxSize().background(Color.White)) {
        Text(
            text = "통계 화면 내용",
            modifier = Modifier.background(Color.White) // 배경색 설정
        )
    }


}

@Preview
@Composable
fun PreviewStatsScreen() {
    StatsScreen()
}