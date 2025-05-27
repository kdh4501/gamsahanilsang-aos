package com.dhkim.gamsahanilsang.domain.model

import com.google.firebase.firestore.DocumentId
import com.google.firebase.firestore.ServerTimestamp
import java.util.Date

data class GratitudeEntry(
    @DocumentId // Firestore 문서 ID를 이 필드에 매핑
    val id: String = "", // 각 기록의 고유 ID (Firestore 문서 ID)
    val userId: String = "", // 이 기록을 작성한 사용자의 ID (Firebase Auth UID)
    val title: String = "", // 감사 기록 제목 (옵션)
    val content: String = "", // 감사 기록 내용 (필수)
    @ServerTimestamp // Firestore 서버 시간을 기록 (생성 시간)
    val timestamp: Date? = null // 기록 생성 시점 (정렬에 유용)
    // TODO: 필요한 필드 추가 (예: 이미지 URL, 태그 등)
)
