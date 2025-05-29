package com.dhkim.gamsahanilsang.presentation.screen

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.input.KeyboardCapitalization
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavController
import com.dhkim.gamsahanilsang.domain.model.GratitudeEntry
import com.dhkim.gamsahanilsang.presentation.viewModel.GratitudeListViewModel
import java.util.Date

@OptIn(ExperimentalMaterial3Api::class) // TopAppBar 사용 시 필요
@Composable
fun AddGratitudeScreen(
    navController: NavController, // NavController를 인자로 받습니다.
    viewModel: GratitudeListViewModel = viewModel(), // GratitudeListViewModel 인스턴스를 가져옵니다. (Hilt 사용 시 @HiltViewModel)
    onEntryAdded: () -> Unit, // 기록 추가 완료 후 호출될 콜백
    onCancel: () -> Unit // 기록 추가 취소 시 호출될 콜백
) {
    // 사용자 입력 내용을 저장할 상태 변수
    var gratitudeContent by remember { mutableStateOf("") }
    // TODO: 제목 필드도 추가한다면 상태 변수 추가
    // var gratitudeTitle by remember { mutableStateOf("") }

    // TODO: 저장 중 로딩 상태 표시를 ViewModel에서 관리한다면 해당 상태 구독

    Scaffold(
        topBar = {
            // 상단 앱 바 (제목, 취소/저장 버튼)
            TopAppBar( // CenterAlignedTopAppBar 대신 Left-aligned TopAppBar 사용
                title = { Text("새 기록 추가") },
                navigationIcon = {
                    // 뒤로가기 (취소) 버튼
                    IconButton(onClick = onCancel) { // onCancel 콜백 호출
                        Icon(Icons.Default.ArrowBack, contentDescription = "취소")
                    }
                },
                actions = {
                    // 저장 버튼
                    TextButton(
                        onClick = {
                            // TODO: 입력 유효성 검사 (내용이 비어있지 않은지 등)
                            if (gratitudeContent.isNotBlank()) {
                                // AppGratitudeEntry 객체 생성 (id는 Firestore/Room이 생성하도록 빈 값/0L)
                                val newEntry = GratitudeEntry(
                                    id = "0", // Room ID 0L에 매핑되거나 Firestore가 자동 생성하도록 의미 없는 값
                                    content = gratitudeContent.trim(), // 내용 앞뒤 공백 제거
                                    // TODO: 제목 필드 추가했다면 title = gratitudeTitle.trim()
                                    timestamp = Date(), // 현재 시간
                                    userId = null // ViewModel에서 현재 로그인된 사용자 UID로 채울 것
                                )

                                // ViewModel의 addGratitudeEntry 함수 호출
                                viewModel.addGratitudeEntry(newEntry)

                                // 기록 추가 완료 후 onEntryAdded 콜백 호출 (화면 이동 등)
                                onEntryAdded()

                            } else {
                                // TODO: 내용이 비어있을 때 사용자에게 알림 (Toast 메시지 등)
                                println("기록 내용이 비어있습니다.")
                            }
                        },
                        enabled = gratitudeContent.isNotBlank() // 내용이 비어있지 않을 때만 저장 버튼 활성화
                    ) {
                        Text("저장")
                    }
                }
            )
        }
    ) { paddingValues -> // Scaffold 패딩 적용
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues) // 필수! Scaffold 패딩 적용
                .padding(16.dp) // 내부 패딩
        ) {
            // 감사 기록 내용 입력 필드
            OutlinedTextField( // 또는 TextField 사용
                value = gratitudeContent,
                onValueChange = { gratitudeContent = it }, // 입력 내용 업데이트
                label = { Text("오늘 감사한 일을 작성해주세요...") }, // 입력 필드 라벨
                modifier = Modifier
                    .fillMaxWidth()
                    .weight(1f), // 남은 공간 모두 사용 (스크롤 가능하도록 LazyColumn 등으로 감쌀 수도 있음)
                keyboardOptions = KeyboardOptions(capitalization = KeyboardCapitalization.Sentences), // 첫 글자 대문자 자동 전환
                // TODO: 필요시 maxLines 설정
            )

            // TODO: 제목 입력 필드 추가 (필요시)
            /*
            OutlinedTextField(
                 value = gratitudeTitle,
                 onValueChange = { gratitudeTitle = it },
                 label = { Text("제목 (선택 사항)") },
                 modifier = Modifier.fillMaxWidth().padding(bottom = 8.dp)
            )
            */
        }
    }
}