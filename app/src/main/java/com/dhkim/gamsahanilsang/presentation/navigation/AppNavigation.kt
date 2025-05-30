package com.dhkim.gamsahanilsang.presentation.navigation

import LoginScreen
import android.util.Log
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.MaterialTheme
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
import com.dhkim.gamsahanilsang.data.datasource.GratitudeDataSource
import com.dhkim.gamsahanilsang.data.datasource.remote.FirestoreGratitudeDataSource
import com.dhkim.gamsahanilsang.data.repository.FirestoreGratitudeRepositoryImpl
import com.dhkim.gamsahanilsang.domain.repository.RemoteGratitudeRepository
import com.dhkim.gamsahanilsang.presentation.screen.AddGratitudeScreen
import com.dhkim.gamsahanilsang.presentation.screen.MainScreen
import com.dhkim.gamsahanilsang.presentation.screen.SettingsScreen
import com.dhkim.gamsahanilsang.presentation.viewModel.AuthState
import com.dhkim.gamsahanilsang.presentation.viewModel.AuthViewModel
import com.dhkim.gamsahanilsang.presentation.viewModel.GratitudeListViewModel

// 네비게이션 경로 정의 (상수로 관리)
object AppDestinations {
    const val LOADING_ROUTE = "loading"
    const val LOGIN_ROUTE = "login"
    const val MAIN_ROUTE = "main"
    const val SETTINGS_ROUTE = "settings"
    const val ADD_GRATITUDE_ROUTE = "add_gratitude" // 새 기록 추가 화면 경로
}

@Composable
fun AppNavigation(
    authViewModel: AuthViewModel = viewModel(), // AuthViewModel 주입
    gratitudeListViewModel: GratitudeListViewModel = viewModel {
        val dataSource: GratitudeDataSource = FirestoreGratitudeDataSource()
        val repository: RemoteGratitudeRepository = FirestoreGratitudeRepositoryImpl(dataSource as FirestoreGratitudeDataSource)
        GratitudeListViewModel(repository)
    }

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
            is AuthState.Error -> {
                // 인증 과정 에러 발생
                // 에러 화면으로 이동 또는 로그인 화면으로 이동 후 에러 메시지 표시
                Log.e("AppNav", "AuthState ${authState}, navigating to LOGIN_ROUTE for error")
                navController.navigate(AppDestinations.LOGIN_ROUTE) { // 에러 시 로그인 화면으로 보내고 에러 메시지 표시 (선택 사항)
                    popUpTo(AppDestinations.LOADING_ROUTE) { inclusive = true }
                    launchSingleTop = true
                }
                // TODO: 에러 메시지를 사용자에게 보여주는 로직 추가 (ViewModel 상태 또는 별도 UI)
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
                // TODO: 로딩 상태에 따른 메시지 표시 (AuthViewModel의 authState 관찰)
                when (authState) {
                    AuthState.Initial -> Text("앱 초기화 중...")
                    AuthState.Loading -> CircularProgressIndicator() // 로딩 스피너
                    is AuthState.Error -> Text("오류 발생: ${(authState as AuthState.Error).message}", color = MaterialTheme.colorScheme.error) // 에러 메시지 표시
                    else -> Text("상태 확인 완료. 이동 중...") // 최종 상태가 되었으나 아직 네비게이션 중일 때
                }
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
            MainScreen(
                navController = navController,
                viewModel = gratitudeListViewModel
            )
        }

        // 새 기록 추가 화면 연결
        composable(AppDestinations.ADD_GRATITUDE_ROUTE) {
            AddGratitudeScreen(
                navController = navController, // NavController 전달
                viewModel = gratitudeListViewModel, // 필요시 ViewModel 전달 (위에서 viewModel()로 가져오고 있다면 필요 없음)
                onEntryAdded = {
                    // 기록 추가 완료 후 메인 화면으로 돌아가기
                    navController.popBackStack()
                },
                onCancel = {
                    // 취소 버튼 클릭 시 이전 화면으로 돌아가기
                    navController.popBackStack()
                }
            )
        }

        // TODO: 설정 화면 등 다른 화면들도 Composable로 정의하고 네비게이션 연결
        // 👇 👇 👇 SettingsScreen Composable 연결 👇 👇 👇
        composable(AppDestinations.SETTINGS_ROUTE) {
            SettingsScreen(
                navController = navController, // NavController 전달
                authViewModel = authViewModel // AuthViewModel 전달
            )
        }
    }
}
