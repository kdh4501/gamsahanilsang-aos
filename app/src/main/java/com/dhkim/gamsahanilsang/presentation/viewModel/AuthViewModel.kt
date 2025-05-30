package com.dhkim.gamsahanilsang.presentation.viewModel

import android.util.Log
import androidx.lifecycle.DefaultLifecycleObserver
import androidx.lifecycle.LifecycleOwner
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.google.firebase.Firebase
import com.google.firebase.auth.AuthCredential
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseAuthUserCollisionException
import com.google.firebase.auth.FirebaseUser
import com.google.firebase.auth.auth
import dagger.hilt.android.lifecycle.HiltViewModel
import jakarta.inject.Inject

import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

@HiltViewModel
class AuthViewModel @Inject constructor() : ViewModel(), DefaultLifecycleObserver {
    private val auth = Firebase.auth
    private val _authState = MutableStateFlow<AuthState>(AuthState.Initial)
    val authState = _authState.asStateFlow()

    private val authStateListener = FirebaseAuth.AuthStateListener { firebaseAuth ->
        val user = firebaseAuth.currentUser
        if (user != null) {
            // 사용자가 로그인되어 있다면 (구글 또는 익명)
            if (user.isAnonymous) {
                // 익명 사용자 상태
                _authState.value = AuthState.Anonymous(user)
                Log.d("AuthVM", "AuthStateListener: User is Anonymous (${user.uid})")
            } else {
                // 구글 등 실제 계정 로그인 상태
                _authState.value = AuthState.Authenticated(user)
                Log.d("AuthVM", "AuthStateListener: User is Authenticated (${user.uid})")
            }
        } else {
            // 사용자 로그아웃 상태 -> 익명 인증 시도 또는 Unauthenticated 상태로 변경
            // 여기서는 익명 인증을 다시 시도하여 항상 어떤 형태든 로그인 상태를 유지
            Log.d("AuthVM", "AuthStateListener: User is signed out. Attempting anonymous sign-in.")
            signInAnonymously() // 로그아웃되면 자동으로 익명 로그인 시도
        }
    }

    init {
//        checkAuthState()
    }

    // DefaultLifecycleObserver 함수 오버라이드
    override fun onStart(owner: LifecycleOwner) {
        super.onStart(owner)
        Log.d("AuthVM", "onStart: Adding AuthStateListener")
        // Activity/Fragment가 시작될 때 리스너 추가
        auth.addAuthStateListener(authStateListener)
    }

    override fun onStop(owner: LifecycleOwner) {
        super.onStop(owner)
        Log.d("AuthVM", "onStop: Removing AuthStateListener")
        // Activity/Fragment가 중지될 때 리스너 제거
        auth.removeAuthStateListener(authStateListener)
    }

    // 익명 인증 함수 (로그인된 사용자 없을 때 자동 호출)
    private fun signInAnonymously() {
        // 이미 익명 로그인 상태라면 다시 시도하지 않음
        if (auth.currentUser != null && auth.currentUser!!.isAnonymous) {
            Log.d("AuthVM", "signInAnonymously called but already anonymous.")
            return
        }
        // 이미 다른 형태로 로그인 상태라면 다시 시도하지 않음
        if (auth.currentUser != null && !auth.currentUser!!.isAnonymous) {
            Log.d("AuthVM", "signInAnonymously called but already authenticated.")
            return
        }

        _authState.value = AuthState.Loading // 로딩 상태로 변경 (익명 로그인 시도 중)
        auth.signInAnonymously()
            .addOnCompleteListener { task ->
                if (task.isSuccessful) {
                    // 익명 인증 성공 -> AuthStateListener가 상태를 Anonymous로 업데이트할 것
                    Log.d("AuthVM", "Anonymous sign in task completed successfully.")
                } else {
                    // 익명 인증 실패 -> AuthStateListener가 상태를 처리하지 않으므로 여기서 Error 상태 업데이트
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
                        // TODO: 마이그레이션 트리거 함수 호출 (나중에)
                    } else {
                        // 계정 연결 실패 (이미 해당 구글 계정에 다른 Firebase 계정이 연결되어 있을 수 있음)
                        Log.e("AuthVM", "Linking anonymous account with Google failed", task.exception)
                        // 다른 계정에 이미 연결되어 있다면 해당 계정으로 로그인 시도
                        if (task.exception is FirebaseAuthUserCollisionException) {
                            auth.signInWithCredential(credential)
                                .addOnCompleteListener { signInTask ->
                                    if (signInTask.isSuccessful) {
                                        // 기존 계정 로그인 성공 -> AuthStateListener가 상태를 Authenticated로 업데이트할 것
                                        Log.d("AuthVM", "Signed in with existing Google account after collision. Listener will update state.")
                                    } else {
                                        // 기존 계정 로그인 실패
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
    object Unauthenticated : AuthState() // 로그인 안 된 상태
    data class Error(val message: String) : AuthState() // 오류 발생
}