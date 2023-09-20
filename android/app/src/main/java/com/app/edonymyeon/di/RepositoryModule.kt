package com.app.edonymyeon.di

import com.app.edonymyeon.data.repository.AuthRepositoryImpl
import com.app.edonymyeon.data.repository.PostRepositoryImpl
import com.app.edonymyeon.data.repository.PreferenceRepositoryImpl
import com.app.edonymyeon.data.repository.RecommendRepositoryImpl
import com.app.edonymyeon.data.repository.ReportRepositoryImpl
import com.domain.edonymyeon.repository.AuthRepository
import com.domain.edonymyeon.repository.PostRepository
import com.domain.edonymyeon.repository.PreferenceRepository
import com.domain.edonymyeon.repository.RecommendRepository
import com.domain.edonymyeon.repository.ReportRepository
import dagger.Binds
import dagger.Module
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent

@Module
@InstallIn(SingletonComponent::class)
abstract class RepositoryModule {
    @Binds
    abstract fun bindPreferenceRepository(
        impl: PreferenceRepositoryImpl,
    ): PreferenceRepository

    @Binds
    abstract fun bindAuthRepository(
        impl: AuthRepositoryImpl,
    ): AuthRepository

    @Binds
    abstract fun bindPostRepository(
        impl: PostRepositoryImpl,
    ): PostRepository

    @Binds
    abstract fun bindRecommendRepository(
        impl: RecommendRepositoryImpl,
    ): RecommendRepository

    @Binds
    abstract fun bindReportRepository(
        impl: ReportRepositoryImpl,
    ): ReportRepository
}
