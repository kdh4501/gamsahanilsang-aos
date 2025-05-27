package com.dhkim.gamsahanilsang.domain.mapper

import com.dhkim.gamsahanilsang.domain.entity.GratitudeItem
import com.dhkim.gamsahanilsang.domain.model.AppGratitudeEntry
import com.dhkim.gamsahanilsang.domain.model.GratitudeEntry
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

// Room GratitudeItem -> AppGratitudeEntry 변환
fun GratitudeItem.toAppGratitudeEntry(): AppGratitudeEntry {
    // Room의 String date를 Date 객체로 변환 (포맷 일치 필요)
    // TODO: DateUtils.getTodayDate()의 포맷과 일치하는 SimpleDateFormat 사용
    val dateFormat = SimpleDateFormat("yyyy-MM-dd", Locale.getDefault()) // 예시 포맷
    val dateObj = try {
        dateFormat.parse(this.date) ?: Date() // 파싱 실패 시 현재 시간 사용
    } catch (e: Exception) {
        Date() // 예외 발생 시 현재 시간 사용
    }

    return AppGratitudeEntry(
        id = this.id.toString(), // Room ID (Long)를 String으로 변환
        content = this.gratitudeText, // 필드명 매핑
        timestamp = dateObj, // Date 변환 결과
        userId = null // Room 데이터는 익명 사용자의 것이므로 userId는 null
    )
}

// AppGratitudeEntry -> Room GratitudeItem 변환 (Firestore -> Room 마이그레이션 시 필요 가능성)
// fun AppGratitudeEntry.toGratitudeItem(): GratitudeItem { ... } // 필요시 구현

// Firestore GratitudeEntry -> AppGratitudeEntry 변환
fun GratitudeEntry.toAppGratitudeEntry(): AppGratitudeEntry {
    return AppGratitudeEntry(
        id = this.id, // Firestore ID (String) 그대로 사용
        content = this.content, // 필드명 매핑
        timestamp = this.timestamp ?: Date(), // Date? -> Date (null이면 현재 시간 사용)
        userId = this.userId // Firestore 데이터에는 userId가 포함되어 있음
    )
}

// AppGratitudeEntry -> Firestore GratitudeEntry 변환 (UI/ViewModel -> Firestore 저장 시 필요)
fun AppGratitudeEntry.toGratitudeEntry(userId: String): GratitudeEntry {
    // AppGratitudeEntry에는 timestamp가 Non-nullable Date지만,
    // Firestore에 저장할 때는 ServerTimestamp를 위해 timestamp를 null로 보내는 경우가 많음.
    // 여기서는 id와 content, userId만 넘기고 timestamp는 Firestore가 채우도록 null로 설정 (또는 그대로 넘기거나)
    return GratitudeEntry(
        id = if (this.id == "0") "" else this.id, // Room에서 온 ID 0은 Firestore에서 빈 문자열로 보내 자동 생성 유도
        userId = userId, // 현재 로그인된 사용자의 실제 UID 사용
        title = "", // TODO: 제목 필드를 AppGratitudeEntry에 추가했다면 여기서 매핑
        content = this.content,
        timestamp = null // Firestore ServerTimestamp 사용을 위해 null로 설정
    )
}