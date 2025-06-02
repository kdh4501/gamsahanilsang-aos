package com.dhkim.gamsahanilsang.presentation.di

import android.content.Context
import androidx.room.Room
import com.dhkim.gamsahanilsang.data.dao.GratitudeDao
import com.dhkim.gamsahanilsang.data.database.GratitudeDatabase
import com.dhkim.gamsahanilsang.data.datasource.remote.FirestoreGratitudeDataSource
import com.dhkim.gamsahanilsang.data.repository.FirestoreGratitudeRepositoryImpl
import com.dhkim.gamsahanilsang.data.repository.RoomGratitudeRepositoryImpl
import com.dhkim.gamsahanilsang.domain.usecase.GratitudeUseCase
import com.dhkim.gamsahanilsang.presentation.common.DialogManager
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton


@Module
@InstallIn(SingletonComponent::class)
object ProvidesModule {
    // Room Database 제공 함수 (기존 AppModule에서 이동)
    @Provides
    @Singleton
    fun provideDatabase(@ApplicationContext context: Context): GratitudeDatabase {
        // Room.databaseBuilder 호출 코드는 DatabaseModule.kt 파일에 두는 것이 더 일반적입니다.
        return Room.databaseBuilder(
            context = context, // Hilt가 주입한 Application Context 사용
            klass = GratitudeDatabase::class.java, // Database 클래스 참조
            name = "gratitude_db" // Database 이름
        )
            // TODO: Room Migration 필요시 addMigrations(...) 추가
            .build() // Database 인스턴스 생성
    }

    // Room DAO 제공 함수 (기존 AppModule에서 이동)
    @Provides
    @Singleton // DAO도 싱글톤으로 제공
    fun provideGratitudeDao(database: GratitudeDatabase): GratitudeDao {
        // 이 함수는 DatabaseModule.kt 파일로 이동하는 것이 더 일반적입니다.
        return database.gratitudeDao()
    }

    // Room Repository 구현체 제공 함수 (기존 AppModule에서 이동)
    @Provides
    @Singleton
    fun provideRoomGratitudeRepositoryImpl(
        gratitudeDao: GratitudeDao // Hilt가 제공한 DAO 주입
    ): RoomGratitudeRepositoryImpl {
        return RoomGratitudeRepositoryImpl(gratitudeDao)
    }

    // Use Case 제공 함수 (기존 AppModule에서 이동)
    @Provides
    // @Singleton // Use Case는 Singleton이 아닐 수도 있음
    fun provideGratitudeUseCase(
        // Repository 인터페이스 주입 (binds 모듈에서 바인딩한 인터페이스 사용)
        repository: com.dhkim.gamsahanilsang.domain.repository.RoomGratitudeRepository // RoomGratitudeRepositoryInterface 임포트 확인
    ): GratitudeUseCase {
        return GratitudeUseCase(repository)
    }

    // DialogManager 제공 함수 (기존 AppModule에서 이동)
    @Provides
    @Singleton // DialogManager도 싱글톤 고려
    fun provideDialogManager(@ApplicationContext context: Context): DialogManager {
        return DialogManager(context)
    }

    // Firestore DataSource 구현체 제공 함수 (기존 AppModule에서 이동)
    @Provides
    @Singleton
    fun provideFirestoreGratitudeDataSource(): FirestoreGratitudeDataSource {
        return FirestoreGratitudeDataSource()
    }

    // Firestore Repository 구현체 제공 함수 (기존 AppModule에서 이동)
    @Provides
    @Singleton
    fun provideFirestoreGratitudeRepositoryImpl(
        dataSource: FirestoreGratitudeDataSource // 위에서 제공한 DataSource 주입
    ): FirestoreGratitudeRepositoryImpl {
        return FirestoreGratitudeRepositoryImpl(dataSource)
    }

    @Provides // 의존성 제공 함수
    @Singleton // 앱 전체에서 하나의 인스턴스만 필요
    fun provideFirebaseAuth(): FirebaseAuth {
        // FirebaseAuth 인스턴스를 가져와서 반환
        return FirebaseAuth.getInstance()
    }

    @Provides // 의존성 제공 함수
    @Singleton // 앱 전체에서 하나의 인스턴스만 필요
    fun provideFirebaseFirestore(): FirebaseFirestore {
        // FirebaseFirestore 인스턴스를 가져와서 반환
        return FirebaseFirestore.getInstance()
    }
}