package com.dhkim.gamsahanilsang.domain.repository

import com.dhkim.gamsahanilsang.domain.model.Tag
import kotlinx.coroutines.flow.Flow

interface TagRepository {
    // 모든 태그 가져오기
    fun getAllTags(): Flow<List<Tag>>

    // 특정 ID의 태그 가져오기
    suspend fun getTagById(tagId: String): Tag?

    // 새 태그 추가
    suspend fun addTag(tag: Tag): Long

    // 태그 업데이트
    suspend fun updateTag(tag: Tag): Int

    // 태그 삭제
    suspend fun deleteTag(tagId: String): Int

    // 감사 항목에 태그 할당
    suspend fun assignTagToEntry(entryId: String, tagId: String)

    // 감사 항목에서 태그 제거
    suspend fun removeTagFromEntry(entryId: String, tagId: String)

    // 감사 항목에 할당된 모든 태그 가져오기
    fun getTagsForEntry(entryId: String): Flow<List<Tag>>

    // 특정 태그가 할당된 모든 감사 항목 ID 가져오기
    fun getEntriesWithTag(tagId: String): Flow<List<String>>
}