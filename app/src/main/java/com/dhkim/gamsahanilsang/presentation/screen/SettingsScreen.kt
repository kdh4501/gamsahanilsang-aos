package com.dhkim.gamsahanilsang.presentation.screen

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
import androidx.compose.material.icons.filled.ExitToApp
import androidx.compose.material.icons.filled.Person
import androidx.compose.material3.Divider
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavController
import com.dhkim.gamsahanilsang.presentation.viewModel.AuthState
import com.dhkim.gamsahanilsang.presentation.viewModel.AuthViewModel

/**
 * 앱의 설정 화면 Composable 함수입니다.
 * 계정 정보, 데이터 관리 등의 설정 항목을 표시합니다.
 * AuthViewModel과 연동하여 로그인 상태에 따라 계정 섹션을 다르게 표시합니다.
 *
 * @param navController NavController: 다른 화면(로그인 화면 등)으로 이동하기 위해 필요합니다.
 * @param authViewModel AuthViewModel: 사용자 인증 상태를 관리하는 ViewModel
 */
@Composable
fun SettingsScreen(
    navController: NavController, // NavController를 인자로 받습니다.
    authViewModel: AuthViewModel = viewModel() // AuthViewModel 인스턴스를 가져옵니다.
) {
    // AuthViewModel의 상태(authState)를 관찰합니다.
    val authState by authViewModel.authState.collectAsState()

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

        // 로그인 상태에 따라 다른 UI 표시
        when (val state = authState) {
            is AuthState.Authenticated -> {
                // 로그인된 상태: 사용자 정보와 로그아웃 버튼 표시
                // 사용자 이름과 이메일 표시
                SettingsItem(
                    title = state.user.displayName ?: "로그인 사용자", // 사용자 이름이 없을 경우 기본값
                    subtitle = state.user.email ?: "이메일 정보 없음", // 이메일 정보 표시
                    icon = Icons.Default.Person, // 사용자 아이콘
                    onClick = {
                        // TODO: 프로필 상세 정보 화면으로 이동 또는 액션 처리 (필요시)
                        println("프로필 정보 클릭!")
                    }
                )

                // 로그아웃 항목
                SettingsItem(
                    title = "로그아웃",
                    subtitle = null, // 부제목 없음
                    icon = Icons.Default.ExitToApp, // 로그아웃 아이콘
                    onClick = {
                        // 로그아웃 버튼 클릭 시 ViewModel의 signOut 함수 호출
                        authViewModel.signOut()
                        println("로그아웃 버튼 클릭! -> signOut 호출")
                    }
                )
            }

            is AuthState.Error -> TODO()
            AuthState.Initial -> TODO()
            AuthState.Loading -> TODO()
            AuthState.Unauthenticated -> TODO()
            is AuthState.Anonymous -> TODO()
        }

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

/**
 * 설정 화면의 개별 항목을 정의하는 Composable 함수입니다.
 * 아이콘, 제목, 부제목을 포함하며 클릭 가능합니다.
 *
 * @param title 항목 제목
 * @param subtitle 항목 부제목 (선택 사항)
 * @param icon 항목 아이콘
 * @param onClick 항목 클릭 시 호출될 콜백
 */
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