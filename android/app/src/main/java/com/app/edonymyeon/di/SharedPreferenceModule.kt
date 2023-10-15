package com.app.edonymyeon.di

import com.app.edonymyeon.data.util.TokenSharedPreference
import com.app.edonymyeon.data.util.TokenSharedPreferenceImpl
import dagger.Binds
import dagger.Module
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
abstract class SharedPreferenceModule {
    @Binds
    @Singleton
    abstract fun provideSharedPreference(
        impl: TokenSharedPreferenceImpl,
    ): TokenSharedPreference
}
