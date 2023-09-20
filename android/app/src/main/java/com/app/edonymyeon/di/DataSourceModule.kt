package com.app.edonymyeon.di

import com.app.edonymyeon.data.datasource.auth.AuthDataSource
import com.app.edonymyeon.data.datasource.auth.AuthLocalDataSource
import com.app.edonymyeon.data.datasource.auth.AuthRemoteDataSource
import com.app.edonymyeon.data.datasource.consumptions.ConsumptionsDataSource
import com.app.edonymyeon.data.datasource.consumptions.ConsumptionsRemoteDataSource
import com.app.edonymyeon.data.datasource.notification.NotificationDataSource
import com.app.edonymyeon.data.datasource.notification.NotificationRemoteDataSource
import com.app.edonymyeon.data.datasource.post.PostDataSource
import com.app.edonymyeon.data.datasource.post.PostRemoteDataSource
import com.app.edonymyeon.data.datasource.preference.PreferenceDataSource
import com.app.edonymyeon.data.datasource.preference.PreferenceRemoteDataSource
import com.app.edonymyeon.data.datasource.profile.ProfileDataSource
import com.app.edonymyeon.data.datasource.profile.ProfileRemoteDataSource
import com.app.edonymyeon.data.datasource.recommend.RecommendDataSource
import com.app.edonymyeon.data.datasource.recommend.RecommendRemoteDataSource
import com.app.edonymyeon.data.datasource.report.ReportDataSource
import com.app.edonymyeon.data.datasource.report.ReportRemoteDataSource
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
    abstract fun bindPostDataSource(
        remoteDataSource: PostRemoteDataSource,
    ): PostDataSource

    @Binds
    abstract fun bindRecommendDataSource(
        remoteDataSource: RecommendRemoteDataSource,
    ): RecommendDataSource

    @Binds
    abstract fun bindReportDataSource(
        remoteDataSource: ReportRemoteDataSource,
    ): ReportDataSource

    @Binds
    abstract fun bindSearchDataSource(
        remoteImpl: SearchRemoteDataSource,
    ): SearchDataSource

    @Binds
    abstract fun bindProfileDataSource(
        remoteImpl: ProfileRemoteDataSource,
    ): ProfileDataSource

    @Binds
    abstract fun bindNotificationDataSource(
        remoteImpl: NotificationRemoteDataSource,
    ): NotificationDataSource

    @Binds
    abstract fun bindConsumptionsDataSource(
        remoteImpl: ConsumptionsRemoteDataSource,
    ): ConsumptionsDataSource
}
