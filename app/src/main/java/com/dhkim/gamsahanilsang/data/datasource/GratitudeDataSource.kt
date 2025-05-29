package com.dhkim.gamsahanilsang.data.datasource

import com.dhkim.gamsahanilsang.domain.model.GratitudeEntry
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.FirebaseFirestoreException
import com.google.firebase.firestore.Query
import kotlinx.coroutines.channels.awaitClose
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.callbackFlow
import kotlinx.coroutines.tasks.await

interface GratitudeDataSource {
    fun getEntries(userId: String): Flow<List<GratitudeEntry>>
    suspend fun addEntry(userId: String, entry: GratitudeEntry): Result<Unit>
    suspend fun updateEntry(userId: String, entry: GratitudeEntry): Result<Unit>
    suspend fun deleteEntry(userId: String, entryId: String): Result<Unit>
}