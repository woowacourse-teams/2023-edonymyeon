package com.app.edonymyeon.di

import com.app.edonymyeon.data.service.AuthService
import com.app.edonymyeon.data.service.ConsumptionsService
import com.app.edonymyeon.data.service.NotificationService
import com.app.edonymyeon.data.service.PostService
import com.app.edonymyeon.data.service.PreferenceService
import com.app.edonymyeon.data.service.ProfileService
import com.app.edonymyeon.data.service.RecommendService
import com.app.edonymyeon.data.service.ReportService
import com.app.edonymyeon.data.service.SearchService
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import retrofit2.Retrofit
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
object ServiceModule {

    @Singleton
    @Provides
    fun provideAuthService(
        retrofit: Retrofit,
    ): AuthService {
        return retrofit.create(AuthService::class.java)
    }

    @Singleton
    @Provides
    fun provideConsumptionsService(
        retrofit: Retrofit,
    ): ConsumptionsService {
        return retrofit.create(ConsumptionsService::class.java)
    }

    @Singleton
    @Provides
    fun provideNotificationService(
        retrofit: Retrofit,
    ): NotificationService {
        return retrofit.create(NotificationService::class.java)
    }

    @Singleton
    @Provides
    fun providePostService(
        retrofit: Retrofit,
    ): PostService {
        return retrofit.create(PostService::class.java)
    }

    @Singleton
    @Provides
    fun providePreferenceService(
        retrofit: Retrofit,
    ): PreferenceService {
        return retrofit.create(PreferenceService::class.java)
    }

    @Singleton
    @Provides
    fun provideProfileService(
        retrofit: Retrofit,
    ): ProfileService {
        return retrofit.create(ProfileService::class.java)
    }

    @Singleton
    @Provides
    fun provideRecommendService(
        retrofit: Retrofit,
    ): RecommendService {
        return retrofit.create(RecommendService::class.java)
    }

    @Singleton
    @Provides
    fun provideReportService(
        retrofit: Retrofit,
    ): ReportService {
        return retrofit.create(ReportService::class.java)
    }

    @Singleton
    @Provides
    fun provideSearchService(
        retrofit: Retrofit,
    ): SearchService {
        return retrofit.create(SearchService::class.java)
    }
}
