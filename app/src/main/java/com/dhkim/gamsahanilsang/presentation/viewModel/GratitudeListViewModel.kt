package com.dhkim.gamsahanilsang.presentation.viewModel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.dhkim.gamsahanilsang.domain.model.GratitudeEntry
import com.dhkim.gamsahanilsang.domain.repository.RemoteGratitudeRepository
import com.google.firebase.auth.FirebaseAuth
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.launch

class GratitudeListViewModel(
    // 생성자를 통해 RemoteGratitudeRepository 의존성을 주입받습니다.
    private val gratitudeRepository: RemoteGratitudeRepository
) : ViewModel() {
    // UI 상태를 나타내는 StateFlow. 이 상태를 UI(Composable)에서 관찰합니다.
    private val _uiState = MutableStateFlow(GratitudeListUiState())
    val uiState: StateFlow<GratitudeListUiState> = _uiState.asStateFlow()

    // Firebase Auth 인스턴스를 가져와서 현재 로그인 사용자의 UID를 얻습니다.
    private val auth = FirebaseAuth.getInstance()

    init {
        // ViewModel이 처음 생성될 때 (즉, 화면이 처음 표시될 때) 감사 기록 로드를 시작합니다.
        loadGratitudeEntries()
    }

    // 감사 기록을 로드하는 함수. 로그인된 사용자의 기록만 가져옵니다.
    private fun loadGratitudeEntries() {
        // 현재 로그인된 사용자의 UID를 가져옵니다.
        val userId = auth.currentUser?.uid ?: run {
            // 만약 로그인되어 있지 않다면 에러 상태로 UI 업데이트 후 함수 종료
            _uiState.value = _uiState.value.copy(
                isLoading = false, // 로딩 끝
                error = "로그인이 필요합니다. 설정을 확인해주세요." // 에러 메시지 설정
            )
            return // 함수 실행 중단
        }

        // ViewModel의 코루틴 스코프에서 비동기 작업 시작
        viewModelScope.launch {
            // 데이터 로딩 시작 상태로 UI 업데이트
            _uiState.value = _uiState.value.copy(isLoading = true, error = null)

            // Repository에서 감사 기록 목록 Flow를 가져와서 수집(collect)합니다.
            // Firestore 데이터가 변경되면 이 Flow가 새로운 데이터를 emit하고, collect 블록이 다시 실행됩니다.
            gratitudeRepository.getGratitudeEntries(userId)
                .catch { e -> // Flow 처리 중 예외 발생 시
                    // 에러 상태로 UI 업데이트
                    _uiState.value = _uiState.value.copy(
                        isLoading = false, // 로딩 끝
                        error = e.message ?: "기록 로딩 중 알 수 없는 오류 발생" // 에러 메시지 설정
                    )
                    // 에러 로그 출력
                    println("기록 로딩 Flow 오류: ${e.message}")
                }
                .collect { entries -> // 데이터가 새로 emit될 때마다
                    // 성공적으로 데이터를 받았으면 UI 상태 업데이트
                    _uiState.value = _uiState.value.copy(
                        isLoading = false, // 로딩 끝
                        entries = entries, // 최신 데이터 반영
                        error = null // 에러 상태 초기화
                    )
                    println("기록 로드 성공: ${entries.size} 개") // 로드된 기록 수 로그
                }
        }
    }

    // 새로운 감사 기록 추가 요청을 처리하는 함수
    fun addGratitudeEntry(entry: GratitudeEntry) {
        // 현재 로그인된 사용자의 UID를 가져와서, 없으면 함수 종료
        val userId = auth.currentUser?.uid ?: run {
            _uiState.value = _uiState.value.copy(error = "로그인 상태를 확인해주세요.")
            return
        }

        // ViewModel의 코루틴 스코프에서 비동기 작업 시작
        viewModelScope.launch {
            // TODO: (옵션) 기록 추가 중 로딩 상태나 UI 피드백 표시 로직 추가

            // Repository에 기록 추가 요청
            val result = gratitudeRepository.addGratitudeEntry(userId, entry)

            // 결과 처리
            result.onSuccess {
                // 성공 시 특별히 할 일 없음. Firestore Flow에서 자동으로 UI가 업데이트됩니다.
                println("기록 추가 성공!")
            }.onFailure { e ->
                // 실패 시 에러 메시지 처리 (예: 토스트 메시지 표시)
                _uiState.value = _uiState.value.copy(error = "기록 추가 실패: ${e.message}")
                println("기록 추가 실패: ${e.message}")
            }
        }
    }

    // 감사 기록 삭제 요청을 처리하는 함수
    fun deleteGratitudeEntry(entryId: String) {
        // 현재 로그인된 사용자의 UID를 가져와서, 없으면 함수 종료
        val userId = auth.currentUser?.uid ?: run {
            _uiState.value = _uiState.value.copy(error = "로그인 상태를 확인해주세요.")
            return
        }

        // ViewModel의 코루틴 스코프에서 비동기 작업 시작
        viewModelScope.launch {
            // TODO: (옵션) 기록 삭제 중 로딩 상태나 UI 피드백 표시 로직 추가

            // Repository에 기록 삭제 요청
            val result = gratitudeRepository.deleteGratitudeEntry(userId, entryId)

            // 결과 처리
            result.onSuccess {
                // 성공 시 특별히 할 일 없음. Firestore Flow에서 자동으로 UI가 업데이트됩니다.
                println("기록 삭제 성공: $entryId")
            }.onFailure { e ->
                // 실패 시 에러 메시지 처리 (예: 토스트 메시지 표시)
                _uiState.value = _uiState.value.copy(error = "기록 삭제 실패: ${e.message}")
                println("기록 삭제 실패: ${e.message}")
            }
        }
    }

    // TODO: 감사 기록 수정, 검색, 필터링 등의 함수 추가
    // TODO: 네비게이션 이벤트 (예: 기록 클릭 시 상세 화면으로 이동) 처리 로직 추가
}

// 감사 기록 목록 화면의 UI 상태를 나타내는 데이터 클래스
data class GratitudeListUiState(
    val isLoading: Boolean = false, // 데이터 로딩 중인지 여부
    val entries: List<GratitudeEntry> = emptyList(), // 화면에 보여줄 감사 기록 목록 데이터
    val error: String? = null // 데이터 로딩 또는 작업 중 발생한 에러 메시지 (없으면 null)
    // TODO: 추가적인 UI 상태 필드 정의 (예: 검색어, 필터 상태 등)
)