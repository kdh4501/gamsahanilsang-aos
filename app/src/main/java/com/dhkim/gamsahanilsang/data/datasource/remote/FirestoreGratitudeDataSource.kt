package com.dhkim.gamsahanilsang.data.datasource.remote

import com.dhkim.gamsahanilsang.data.datasource.GratitudeDataSource
import com.dhkim.gamsahanilsang.domain.model.GratitudeEntry
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.FirebaseFirestoreException
import com.google.firebase.firestore.Query
import kotlinx.coroutines.channels.awaitClose
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.callbackFlow
import kotlinx.coroutines.tasks.await
import java.util.Date


// Firestore 기반 감사 기록 데이터 소스 구현체
class FirestoreGratitudeDataSource : GratitudeDataSource {

    private val firestore = FirebaseFirestore.getInstance()
    private val collectionName = "gratitude_entries" // Firestore 컬렉션 이름

    // 특정 사용자의 감사 기록 목록 실시간 스트림 제공
    override fun getEntries(userId: String): Flow<List<GratitudeEntry>> = callbackFlow {
        val subscription = firestore.collection(collectionName)
            .whereEqualTo("userId", userId) // 현재 사용자의 기록만 필터링
            .orderBy("timestamp", Query.Direction.DESCENDING) // 최신 기록 먼저 보이도록 정렬
            .addSnapshotListener { snapshot, exception ->
                if (exception != null) {
                    // 에러 발생 시 Flow에 에러 전달
                    close(exception)
                    return@addSnapshotListener
                }

                if (snapshot != null) {
                    // 스냅샷을 GratitudeEntry 리스트로 변환하여 Flow에 emit
                    val entries = snapshot.toObjects(GratitudeEntry::class.java)
                    trySend(entries)
                } else {
                    // 스냅샷이 null이면 빈 리스트 emit
                    trySend(emptyList())
                }
            }

        // Flow가 취소될 때 스냅샷 리스너 해제
        awaitClose { subscription.remove() }
    }

    // 👇👇👇 기간 필터링 쿼리 로직 함수 추가 👇👇👇
    fun getEntriesByDateRange(
        userId: String,
        startDate: Date? = null,
        endDate: Date? = null
    ): Flow<List<GratitudeEntry>> = callbackFlow {
        var query: Query = firestore.collection(collectionName)
            .whereEqualTo("userId", userId) // 사용자 ID 필터링 (필수)

        // 시작 날짜/시간 필터 추가
        if (startDate != null) {
            query = query.whereGreaterThanOrEqualTo("timestamp", startDate)
        }

        // 종료 날짜/시간 필터 추가
        if (endDate != null) {
            query = query.whereLessThanOrEqualTo("timestamp", endDate)
        }

        // 정렬 조건 추가 (기간 필터링 시에도 보통 시간 순서 정렬)
        // Firestore 쿼리는 필터링된 필드(timestamp)와 정렬 필드(timestamp)가 같거나
        // 첫 번째 정렬 필드와 첫 번째 필터링 필드가 같아야 합니다. (복합 인덱스 필요)
        // 현재 userId로 필터링하고 timestamp로 필터링/정렬하므로 복합 인덱스 필요.
        // 이전에 timestamp와 userId로 인덱스 만들었으니 문제 없을 것.
        query = query.orderBy("timestamp", Query.Direction.DESCENDING)
        // TODO: 필요시 secondary sort 필드 추가 (예: 문서 ID)
        // query = query.orderBy("__name__", Query.Direction.ASCENDING)


        val subscription = query.addSnapshotListener { snapshot, exception ->
            if (exception != null) {
                close(exception)
                return@addSnapshotListener
            }
            snapshot?.toObjects(GratitudeEntry::class.java)?.let { entries ->
                trySend(entries)
            } ?: trySend(emptyList())
        }

        awaitClose { subscription.remove() }
    }

    // 새로운 감사 기록 추가
    override suspend fun addEntry(userId: String, entry: GratitudeEntry): Result<Unit> = try {
        // ID가 없으면 Firestore가 자동으로 생성
        firestore.collection(collectionName)
            .add(entry.copy(userId = userId)) // entry에 userId 추가하여 저장
            .await() // 작업 완료까지 대기
        Result.success(Unit) // 성공 반환
    } catch (e: FirebaseFirestoreException) {
        // 에러 발생 시 실패 반환
        Result.failure(e)
    } catch (e: Exception) {
        Result.failure(e)
    }

    // 기존 감사 기록 업데이트
    override suspend fun updateEntry(userId: String, entry: GratitudeEntry): Result<Unit> = try {
        firestore.collection(collectionName).document(entry.id)
            .set(entry) // 문서 ID로 해당 문서 업데이트
            .await()
        Result.success(Unit)
    } catch (e: FirebaseFirestoreException) {
        Result.failure(e)
    } catch (e: Exception) {
        Result.failure(e)
    }

    // 감사 기록 삭제
    override suspend fun deleteEntry(userId: String, entryId: String): Result<Unit> = try {
        firestore.collection(collectionName).document(entryId)
            .delete() // 문서 ID로 해당 문서 삭제
            .await()
        Result.success(Unit)
    } catch (e: FirebaseFirestoreException) {
        Result.failure(e)
    } catch (e: Exception) {
        Result.failure(e)
    }

    // TODO: 마이그레이션 시 로컬 데이터를 Firestore에 벌크로 추가하는 함수 추가 고려
    suspend fun addEntriesBulk(entries: List<GratitudeEntry>): Result<Unit> = try {
        val batch = firestore.batch()
        entries.forEach { entry ->
            // 새로운 문서 참조 생성 (ID 자동 생성)
            val docRef = firestore.collection(collectionName).document()
            batch.set(docRef, entry)
        }
        batch.commit().await()
        Result.success(Unit)
    } catch (e: Exception) { Result.failure(e) }
}