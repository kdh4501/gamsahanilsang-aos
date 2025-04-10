package com.dhkim.gamsahanilsang.presentation.activity.detil

import androidx.compose.material3.Text
import androidx.compose.runtime.Composable

@Composable
fun DetailScreen(itemId: String?) {
    // itemId를 사용하여 데이터베이스에서 항목을 가져오는 로직 추가
    // 예: ViewModel에서 itemId에 해당하는 GratitudeItem을 가져와서 보여줍니다.

    // UI 구성
    Text(text = "Detail for item: $itemId") // 간단한 텍스트로 표시
}