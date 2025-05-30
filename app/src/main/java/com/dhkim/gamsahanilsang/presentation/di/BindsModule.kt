package com.dhkim.gamsahanilsang.presentation.di

import com.dhkim.gamsahanilsang.data.repository.FirestoreGratitudeRepositoryImpl
import com.dhkim.gamsahanilsang.data.repository.RoomGratitudeRepositoryImpl
import com.dhkim.gamsahanilsang.domain.repository.RemoteGratitudeRepository
import com.dhkim.gamsahanilsang.domain.repository.RoomGratitudeRepository
import dagger.Binds
import dagger.Module
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton


@Module // Hilt 모듈
@InstallIn(SingletonComponent::class)
abstract class BindsModule {
    // Room Repository 인터페이스 바인딩 (기존 AppModule에서 이동)
    @Binds // 인터페이스를 구현체에 바인딩하는 추상 함수
    @Singleton // 제공하는 의존성의 스코프 (Provides 모듈의 스코프와 일치)
    abstract fun bindRoomGratitudeRepository(
        // Hilt가 ProvidesModule에서 제공하는 RoomGratitudeRepositoryImpl 인스턴스
        impl: RoomGratitudeRepositoryImpl
    ): RoomGratitudeRepository // Hilt가 이 타입 요청 시 impl 인스턴스 제공

    // Firestore Repository 인터페이스 바인딩 (기존 AppModule에서 이동)
    @Binds // 인터페이스를 구현체에 바인딩하는 추상 함수
    @Singleton // 제공하는 의존성의 스코프
    abstract fun bindFirestoreGratitudeRepository(
        // Hilt가 ProvidesModule에서 제공하는 FirestoreGratitudeRepositoryImpl 인스턴스
        impl: FirestoreGratitudeRepositoryImpl
    ): RemoteGratitudeRepository
}