package com.app.edonymyeon.di

import com.app.edonymyeon.data.repository.AuthRepositoryImpl
import com.app.edonymyeon.data.repository.PreferenceRepositoryImpl
import com.app.edonymyeon.data.repository.SearchRepositoryImpl
import com.domain.edonymyeon.repository.AuthRepository
import com.domain.edonymyeon.repository.PreferenceRepository
import com.domain.edonymyeon.repository.SearchRepository
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
    abstract fun bindSearchRepository(
        impl: SearchRepositoryImpl,
    ): SearchRepository
}
