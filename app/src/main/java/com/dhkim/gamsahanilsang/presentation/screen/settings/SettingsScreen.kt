package com.dhkim.gamsahanilsang.presentation.screen.settings

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.AccountCircle
import androidx.compose.material.icons.filled.Backup
import androidx.compose.material.icons.filled.Download
import androidx.compose.material3.Divider
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.unit.dp

@Composable
fun SettingsScreen() {
    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(MaterialTheme.colorScheme.background)
            .padding(16.dp)
    ) {
        // 로그인 섹션
        Text(
            text = "계정",
            style = MaterialTheme.typography.titleMedium,
            modifier = Modifier.padding(vertical = 8.dp)
        )

        // 로그인 항목
        SettingsItem(
            title = "로그인",
            subtitle = "구글 계정으로 로그인하여 데이터를 동기화하세요",
            icon = Icons.Default.AccountCircle,
            onClick = { /* 로그인 처리 */ }
        )

        Divider(modifier = Modifier.padding(vertical = 8.dp))

        // 기타 설정 항목들...
        // 데이터 관리 섹션 추가
        Text(
            text = "데이터 관리",
            style = MaterialTheme.typography.titleMedium,
            modifier = Modifier.padding(vertical = 8.dp)
        )

        // 백업 및 복원 항목
        SettingsItem(
            title = "백업 및 복원",
            subtitle = "감사 기록을 클라우드에 백업하고 복원하세요",
            icon = Icons.Default.Backup, // 적절한 아이콘 사용
            onClick = { /* 백업 및 복원 처리 */ }
        )

        // 데이터 내보내기
        SettingsItem(
            title = "데이터 내보내기",
            subtitle = "감사 기록을 CSV 또는 PDF로 내보내기",
            icon = Icons.Default.Download, // 적절한 아이콘 사용
            onClick = { /* 내보내기 처리 */ }
        )
    }
}

@Composable
fun SettingsItem(
    title: String,
    subtitle: String? = null,
    icon: ImageVector,
    onClick: () -> Unit
) {
    Surface(
        modifier = Modifier
            .fillMaxWidth()
            .clickable(onClick = onClick),
        color = MaterialTheme.colorScheme.surface
    ) {
        Row(
            modifier = Modifier
                .padding(vertical = 12.dp, horizontal = 16.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Icon(
                imageVector = icon,
                contentDescription = null,
                tint = MaterialTheme.colorScheme.primary
            )
            Spacer(modifier = Modifier.width(16.dp))
            Column {
                Text(
                    text = title,
                    style = MaterialTheme.typography.bodyLarge
                )
                if (subtitle != null) {
                    Text(
                        text = subtitle,
                        style = MaterialTheme.typography.bodyMedium,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                }
            }
        }
    }
}