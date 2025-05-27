package com.dhkim.gamsahanilsang.domain.model

import java.util.Date

// 앱 전체 (UI, ViewModel, Domain)에서 사용할 공통 감사 기록 모델
data class AppGratitudeEntry(
    // Room과 Firestore 모두에서 고유 식별자로 사용될 수 있는 필드
    // Room의 Long ID와 Firestore의 String ID를 모두 표현할 수 있어야 함
    val id: String, // Firestore ID를 String으로 사용, Room ID는 String으로 변환하여 저장
    val content: String, // 감사 기록 내용 (GratitudeItem의 gratitudeText와 매핑)
    val timestamp: Date, // 기록 시점 (Date 타입, GratitudeItem의 date와 GratitudeEntry의 timestamp 모두 Date로 변환하여 저장)
    val userId: String? = null // 로그인 사용자 ID (익명 사용자는 null)
    // TODO: 필요한 경우 다른 필드 추가 (예: title 등)
)
