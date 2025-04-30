package com.dhkim.gamsahanilsang.domain.model

import java.util.UUID

data class Tag(
    val id: String = UUID.randomUUID().toString(),
    val name: String,
    val color: Int,
    val createdAt: Long = System.currentTimeMillis()
) {
    companion object {
        // 기본 태그 생성 헬퍼 함수
        fun createDefault(name: String, color: Int): Tag {
            return Tag(name = name, color = color)
        }
    }
}
