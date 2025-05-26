package com.dhkim.gamsahanilsang.presentation.navigation

import LoginScreen
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import com.dhkim.gamsahanilsang.presentation.viewModel.AuthState
import com.dhkim.gamsahanilsang.presentation.viewModel.AuthViewModel

// 네비게이션 경로 정의 (상수로 관리)
object AppDestinations {
    const val LOADING_ROUTE = "loading"
    const val LOGIN_ROUTE = "login"
    const val MAIN_ROUTE = "main"
    // TODO: 다른 화면 경로 추가 (예: settings, detail 등)
}

@Composable
fun AppNavigation(
    authViewModel: AuthViewModel = viewModel() // AuthViewModel 주입
) {
    val navController = rememberNavController() // NavController 생성
    val authState by authViewModel.authState.collectAsState() // 로그인 상태 관찰

    // LaunchedEffect를 사용하여 authState 변경 시 네비게이션 실행 (로딩 상태 처리)
    LaunchedEffect(authState) {
        when (authState) {
            is AuthState.Authenticated -> {
                navController.navigate(AppDestinations.MAIN_ROUTE) {
                    popUpTo(AppDestinations.LOADING_ROUTE) { inclusive = true }
                    launchSingleTop = true
                }
            }
            is AuthState.Unauthenticated -> {
                navController.navigate(AppDestinations.LOGIN_ROUTE) {
                    popUpTo(AppDestinations.LOADING_ROUTE) { inclusive = true }
                    launchSingleTop = true
                }
            }
            // Initial, Loading, Error 상태는 LaunchedEffect 밖에서 NavHost의 startDestination으로 처리
            else -> { /* Initial, Loading, Error 상태는 startDestination에서 처리됨 */ }
        }
    }

    NavHost(
        navController = navController,
        // 초기 상태에 따라 startDestination 설정
        startDestination = when (authState) {
            is AuthState.Authenticated -> AppDestinations.MAIN_ROUTE
            is AuthState.Unauthenticated -> AppDestinations.LOGIN_ROUTE
            // Initial, Loading, Error 상태일 때는 로딩 화면으로 시작
            else -> AppDestinations.LOADING_ROUTE
        }
    ) {
        // 로딩 화면 (선택 사항)
        composable(AppDestinations.LOADING_ROUTE) {
            Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                CircularProgressIndicator()
                Text("로그인 상태 확인 중...")
            }
        }

        // 로그인 화면
        composable(AppDestinations.LOGIN_ROUTE) {
            LoginScreen(
                onLoginSuccess = {
                    // 로그인 성공 시 메인 화면으로 이동
                    navController.navigate(AppDestinations.MAIN_ROUTE) {
                        popUpTo(AppDestinations.LOGIN_ROUTE) { inclusive = true } // 로그인 화면 스택에서 제거
                        launchSingleTop = true // 중복 화면 생성 방지
                    }
                }
            )
        }

        // 메인 화면 (감사 기록 목록 등)
        composable(AppDestinations.MAIN_ROUTE) {
            // TODO: 메인 화면 Composable 구현 및 연결
            Text("메인 화면 - 로그인 성공!") // 임시 UI
            // 예시: GratitudeListScreen(navController = navController)
        }

        // TODO: 설정 화면 등 다른 화면들도 Composable로 정의하고 네비게이션 연결
        // composable(AppDestinations.SETTINGS_ROUTE) { SettingsScreen(navController = navController) }
    }
}
