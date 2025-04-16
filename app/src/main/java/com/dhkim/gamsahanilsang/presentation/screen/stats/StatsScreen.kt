package com.dhkim.gamsahanilsang.presentation.screen.stats

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.dhkim.gamsahanilsang.presentation.viewModel.MainViewModel
import ir.ehsannarmani.compose_charts.ColumnChart
import ir.ehsannarmani.compose_charts.models.BarProperties
import ir.ehsannarmani.compose_charts.models.Bars
import ir.ehsannarmani.compose_charts.models.DrawStyle

@Composable
fun StatsScreen(viewModel: MainViewModel) {
    val gratitudeData = mapOf("2023-04-01" to 5, "2023-04-02" to 3, "2023-04-03" to 8)

    // ColumnChart 데이터 준비
    val chartData = remember {
        gratitudeData.map { (date, count) ->
            Bars(
                label = date,
                values = listOf(
                    Bars.Data(value = count.toDouble(), color = Brush.verticalGradient(listOf(Color.Blue, Color.Blue)))
                )
            )
        }
    }

    Box(modifier = Modifier.fillMaxSize().background(Color.White)) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(16.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Text(
                text = "통계 화면",
                style = MaterialTheme.typography.titleMedium,
                modifier = Modifier.padding(bottom = 16.dp)
            )

            Spacer(modifier = Modifier.height(100.dp))   // 사이 여백

            ColumnChart(
                data = chartData,
                barProperties = BarProperties(
                    thickness = 15.dp, // 두께 설정
                    spacing = 6.dp, // 간격 설정
                    cornerRadius = Bars.Data.Radius.None, // 모서리 반경 설정
                    style = DrawStyle.Fill // 스타일 設定
                ),
                modifier = Modifier
                    .fillMaxWidth()
                    .height(300.dp)
            )

        }
    }
}

@Preview
@Composable
fun PreviewStatsScreen() {
    StatsScreen(viewModel = MainViewModel(
        gratitudeUseCase = TODO()
    )) // Mock ViewModel 사용
}