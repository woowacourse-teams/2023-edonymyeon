package com.app.edonymyeon.di

import com.app.edonymyeon.data.datasource.auth.AuthDataSource
import com.app.edonymyeon.data.datasource.auth.AuthLocalDataSource
import com.app.edonymyeon.data.datasource.auth.AuthRemoteDataSource
import com.app.edonymyeon.data.datasource.preference.PreferenceDataSource
import com.app.edonymyeon.data.datasource.preference.PreferenceRemoteDataSource
import com.app.edonymyeon.data.datasource.search.SearchDataSource
import com.app.edonymyeon.data.datasource.search.SearchRemoteDataSource
import dagger.Binds
import dagger.Module
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent

@Module
@InstallIn(SingletonComponent::class)
abstract class DataSourceModule {
    @Binds
    abstract fun bindPreferenceDataSource(
        remoteImpl: PreferenceRemoteDataSource,
    ): PreferenceDataSource

    @Binds
    abstract fun bindAuthRemoteDataSource(
        remoteImpl: AuthRemoteDataSource,
    ): AuthDataSource.Remote

    @Binds
    abstract fun bindAuthLocalDataSource(
        remoteImpl: AuthLocalDataSource,
    ): AuthDataSource.Local

    @Binds
    abstract fun bindSearchDataSource(
        remoteImpl: SearchRemoteDataSource,
    ): SearchDataSource
}
