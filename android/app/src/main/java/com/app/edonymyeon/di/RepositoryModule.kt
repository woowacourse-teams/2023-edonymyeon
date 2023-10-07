package com.app.edonymyeon.di

import com.app.edonymyeon.data.repository.AuthRepositoryImpl
import com.app.edonymyeon.data.repository.ConsumptionsRepositoryImpl
import com.app.edonymyeon.data.repository.NotificationRepositoryImpl
import com.app.edonymyeon.data.repository.PostRepositoryImpl
import com.app.edonymyeon.data.repository.PreferenceRepositoryImpl
import com.app.edonymyeon.data.repository.ProfileRepositoryImpl
import com.app.edonymyeon.data.repository.RecommendRepositoryImpl
import com.app.edonymyeon.data.repository.ReportRepositoryImpl
import com.app.edonymyeon.data.repository.SearchRepositoryImpl
import com.domain.edonymyeon.repository.AuthRepository
import com.domain.edonymyeon.repository.ConsumptionsRepository
import com.domain.edonymyeon.repository.NotificationRepository
import com.domain.edonymyeon.repository.PostRepository
import com.domain.edonymyeon.repository.PreferenceRepository
import com.domain.edonymyeon.repository.ProfileRepository
import com.domain.edonymyeon.repository.RecommendRepository
import com.domain.edonymyeon.repository.ReportRepository
import com.domain.edonymyeon.repository.SearchRepository
import dagger.Binds
import dagger.Module
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
abstract class RepositoryModule {

    @Singleton
    @Binds
    abstract fun bindPreferenceRepository(
        impl: PreferenceRepositoryImpl,
    ): PreferenceRepository

    @Singleton
    @Binds
    abstract fun bindAuthRepository(
        impl: AuthRepositoryImpl,
    ): AuthRepository

    @Singleton
    @Binds
    abstract fun bindSearchRepository(
        impl: SearchRepositoryImpl,
    ): SearchRepository

    @Singleton
    @Binds
    abstract fun bindPostRepository(
        impl: PostRepositoryImpl,
    ): PostRepository

    @Singleton
    @Binds
    abstract fun bindRecommendRepository(
        impl: RecommendRepositoryImpl,
    ): RecommendRepository

    @Singleton
    @Binds
    abstract fun bindReportRepository(
        impl: ReportRepositoryImpl,
    ): ReportRepository

    @Singleton
    @Binds
    abstract fun bindNotificationRepository(
        impl: NotificationRepositoryImpl,
    ): NotificationRepository

    @Singleton
    @Binds
    abstract fun bindProfileRepository(
        impl: ProfileRepositoryImpl,
    ): ProfileRepository

    @Singleton
    @Binds
    abstract fun bindConsumptionsRepository(
        impl: ConsumptionsRepositoryImpl,
    ): ConsumptionsRepository
}
