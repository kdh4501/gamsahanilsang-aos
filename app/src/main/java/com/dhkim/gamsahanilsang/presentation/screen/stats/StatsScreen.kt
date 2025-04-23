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
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.dhkim.gamsahanilsang.domain.entity.GratitudeItem
import com.dhkim.gamsahanilsang.utils.DateUtils
import com.dhkim.gamsahanilsang.presentation.viewModel.MainViewModel
import ir.ehsannarmani.compose_charts.ColumnChart
import ir.ehsannarmani.compose_charts.models.BarProperties
import ir.ehsannarmani.compose_charts.models.Bars
import ir.ehsannarmani.compose_charts.models.DrawStyle

@Composable
fun StatsScreen(mainViewModel: MainViewModel = viewModel()) {
    // groupedGratitudes LiveData를 Compose State로 변환
    val groupedGratitudes by mainViewModel.groupedGratitudes.collectAsState()
    // ColumnChart 데이터 준비
    val chartData = remember(groupedGratitudes) {
        groupedGratitudes.map { (date, gratitudeItems) ->
            Bars(
                label = DateUtils.formatDateLabel(date),
                values = listOf(
                    Bars.Data(
                        value = gratitudeItems.size.toDouble(),
                        color = Brush.verticalGradient(listOf(Color.Blue, Color.Blue))
                    )
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
                    style = DrawStyle.Fill // 스타일
                ),
                modifier = Modifier
                    .fillMaxWidth()
                    .height(300.dp)
            )

        }
    }
}

@Composable
fun StatsScreenPreviewContent() {
    // 임의의 mock 데이터 생성
    val mockGroupedGratitudes: Map<String, List<GratitudeItem>>  = mapOf(
        "2023-04-01" to listOf(GratitudeItem(id = 1L, "감사1", "2023-04-01")),
        "2023-04-02" to listOf(GratitudeItem(id = 2L, "감사2", "2023-04-02"), GratitudeItem(id = 3L, "감사3", "2023-04-02"))
    )

    // 차트 데이터 생성 (StatsScreen 내부 로직과 동일)
    val chartData = mockGroupedGratitudes.map { (date, items) ->
        Bars(
            label = date,
            values = listOf(
                Bars.Data(value = items.size.toDouble(), color = Brush.verticalGradient(listOf(Color.Blue, Color.Cyan)))
            )
        )
    }

    ColumnChart(
        data = chartData,
        barProperties = BarProperties(
            thickness = 15.dp,
            spacing = 6.dp,
            cornerRadius = Bars.Data.Radius.None,
            style = DrawStyle.Fill
        ),
        modifier = Modifier
            .fillMaxWidth()
            .height(300.dp)
    )
}

@Preview(showBackground = true)
@Composable
fun PreviewStatsScreen() {
    Box(modifier = Modifier.fillMaxSize().background(Color.White).padding(16.dp)) {
        Column {
            Text("통계 화면 (Preview)", style = MaterialTheme.typography.titleLarge)
            Spacer(modifier = Modifier.height(16.dp))
            StatsScreenPreviewContent()
        }
    }
}