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
import com.app.edonymyeon.data.service.client.RetrofitClient
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
object ServiceModule {
    private val retrofit = RetrofitClient.getInstance()

    @Singleton
    @Provides
    fun provideAuthService(): AuthService {
        return retrofit.create(AuthService::class.java)
    }

    @Singleton
    @Provides
    fun provideConsumptionsService(): ConsumptionsService {
        return retrofit.create(ConsumptionsService::class.java)
    }

    @Singleton
    @Provides
    fun provideNotificationService(): NotificationService {
        return retrofit.create(NotificationService::class.java)
    }

    @Singleton
    @Provides
    fun providePostService(): PostService {
        return retrofit.create(PostService::class.java)
    }

    @Singleton
    @Provides
    fun providePreferenceService(): PreferenceService {
        return retrofit.create(PreferenceService::class.java)
    }

    @Singleton
    @Provides
    fun provideProfileService(): ProfileService {
        return retrofit.create(ProfileService::class.java)
    }

    @Singleton
    @Provides
    fun provideRecommendService(): RecommendService {
        return retrofit.create(RecommendService::class.java)
    }

    @Singleton
    @Provides
    fun provideReportService(): ReportService {
        return retrofit.create(ReportService::class.java)
    }

    @Singleton
    @Provides
    fun provideSearchService(): SearchService {
        return retrofit.create(SearchService::class.java)
    }
}
