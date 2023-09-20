package com.app.edonymyeon.di

import com.app.edonymyeon.data.repository.AuthRepositoryImpl
import com.app.edonymyeon.data.repository.ConsumptionsRepositoryImpl
import com.app.edonymyeon.data.repository.NotificationRepositoryImpl
import com.app.edonymyeon.data.repository.PostRepositoryImpl
import com.app.edonymyeon.data.repository.PreferenceRepositoryImpl
import com.domain.edonymyeon.repository.AuthRepository
import com.domain.edonymyeon.repository.ConsumptionsRepository
import com.domain.edonymyeon.repository.NotificationRepository
import com.domain.edonymyeon.repository.PostRepository
import com.domain.edonymyeon.repository.PreferenceRepository
import com.domain.edonymyeon.repository.ProfileRepository
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
    abstract fun bindNotificationRepository(
        impl: NotificationRepositoryImpl,
    ): NotificationRepository

    @Binds
    abstract fun bindPostRepository(
        impl: PostRepositoryImpl,
    ): PostRepository

    @Binds
    abstract fun bindProfileRepository(
        impl: PostRepositoryImpl,
    ): ProfileRepository

    @Binds
    abstract fun bindConsumptionsRepository(
        impl: ConsumptionsRepositoryImpl,
    ): ConsumptionsRepository
}
