package com.dhkim.gamsahanilsang.presentation.viewModel

import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.google.firebase.Firebase
import com.google.firebase.auth.AuthCredential
import com.google.firebase.auth.FirebaseUser
import com.google.firebase.auth.auth

import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

class AuthViewModel: ViewModel() {
    private val auth = Firebase.auth
    private val _authState = MutableStateFlow<AuthState>(AuthState.Initial)
    val authState = _authState.asStateFlow()

    init {
        checkAuthState()
    }

    private fun checkAuthState() {
        val currentUser = auth.currentUser
        if (currentUser != null) {
            // 사용자가 로그인 되어 있다면 (구글 또는 익명)
            if (currentUser.isAnonymous) {
                // 익명 사용자 상태
                _authState.value = AuthState.Anonymous(currentUser)
                Log.d("AuthVM", "Current user is Anonymous")
            } else {
                // 구글 등 실제 계정 로그인 상태
                _authState.value = AuthState.Authenticated(currentUser)
                Log.d("AuthVM", "Current user is Authenticated: ${currentUser.uid}")
            }
        } else {
            // 로그인된 사용자 없음 -> 익명 인증 시도
            signInAnonymously()
        }
    }

    // 익명 인증 함수 (로그인된 사용자 없을 때 자동 호출)
    private fun signInAnonymously() {
        _authState.value = AuthState.Loading // 로딩 상태로 변경
        auth.signInAnonymously()
            .addOnCompleteListener { task ->
                if (task.isSuccessful) {
                    // 익명 인증 성공
                    val user = task.result?.user
                    if (user != null) {
                        _authState.value = AuthState.Anonymous(user)
                        Log.d("AuthVM", "Anonymous sign in successful: ${user.uid}")
                    } else {
                        // user 객체가 null인 경우 (발생 가능성 낮음)
                        _authState.value = AuthState.Error("익명 로그인 실패: 사용자 정보 없음")
                        Log.e("AuthVM", "Anonymous sign in successful but user is null")
                    }
                } else {
                    // 익명 인증 실패 (Firebase 설정 확인 필요)
                    _authState.value = AuthState.Error("익명 로그인 실패: ${task.exception?.message}")
                    Log.e("AuthVM", "Anonymous sign in failed", task.exception)
                }
            }
    }

    // 구글 계정으로 로그인 또는 기존 익명 계정에 연결하는 함수
    fun signInWithGoogle(credential: AuthCredential) {
        _authState.value = AuthState.Loading

        // 현재 익명 사용자인지 확인
        val currentUser = auth.currentUser
        if (currentUser != null && currentUser.isAnonymous) {
            // 익명 사용자라면 기존 익명 계정에 구글 자격 증명 연결
            currentUser.linkWithCredential(credential)
                .addOnCompleteListener { task ->
                    if (task.isSuccessful) {
                        // 계정 연결 성공 -> 이제 실제 Firebase 계정 사용자가 됨
                        Log.d("AuthVM", "Linking anonymous account with Google successful")
                        checkAuthState() // 상태 다시 확인 (Authenticated로 바뀔 것)
                        // TODO: 마이그레이션 트리거 함수 호출 (나중에)
                    } else {
                        // 계정 연결 실패 (이미 해당 구글 계정에 다른 Firebase 계정이 연결되어 있을 수 있음)
                        Log.e("AuthVM", "Linking anonymous account with Google failed", task.exception)
                        // 다른 계정에 이미 연결되어 있다면 해당 계정으로 로그인 시도
                        if (task.exception is com.google.firebase.auth.FirebaseAuthUserCollisionException) {
                            auth.signInWithCredential(credential)
                                .addOnCompleteListener { signInTask ->
                                    if (signInTask.isSuccessful) {
                                        Log.d("AuthVM", "Signed in with existing Google account after collision")
                                        checkAuthState() // 상태 다시 확인 (Authenticated로 바뀔 것)
                                    } else {
                                        Log.e("AuthVM", "Sign in with existing Google account failed", signInTask.exception)
                                        _authState.value = AuthState.Error("로그인 실패: ${signInTask.exception?.message}")
                                    }
                                }
                        } else {
                            _authState.value = AuthState.Error("계정 연결 실패: ${task.exception?.message}")
                        }
                    }
                }
        } else {
            // 익명 사용자가 아니거나(이미 로그인) 로그인된 사용자 없다면 새로 구글 로그인 시도
            auth.signInWithCredential(credential)
                .addOnCompleteListener { task ->
                    if (task.isSuccessful) {
                        // 구글 로그인 성공 -> 이제 실제 Firebase 계정 사용자가 됨
                        Log.d("AuthVM", "Google sign in successful")
                        checkAuthState() // 상태 다시 확인 (Authenticated로 바뀔 것)
                        // TODO: 마이그레이션 트리거 함수 호출 (나중에)
                    } else {
                        // 구글 로그인 실패
                        _authState.value = AuthState.Error("로그인 실패: ${task.exception?.message}")
                        Log.e("AuthVM", "Google sign in failed", task.exception)
                    }
                }
        }
    }

    // 로그아웃 함수 (익명 사용자도 로그아웃됨)
    fun signOut() {
        viewModelScope.launch {
            auth.signOut() // Firebase에서 로그아웃
            Log.d("AuthVM", "User signed out")
            // 로그아웃 후 다시 익명 로그인 시도하여 익명 상태 유지
            signInAnonymously()
        }
    }
}

sealed class AuthState {
    object Initial : AuthState()
    object Loading : AuthState()
    data class Authenticated(val user: FirebaseUser) : AuthState() // 구글 등 계정으로 인증됨
    data class Anonymous(val user: FirebaseUser) : AuthState() // 익명 사용자로 인증됨
    data class Error(val message: String) : AuthState() // 오류 발생
}