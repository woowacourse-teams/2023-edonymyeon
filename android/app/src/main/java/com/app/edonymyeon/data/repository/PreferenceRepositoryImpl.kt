package com.app.edonymyeon.data.repository

import com.app.edonymyeon.data.common.CustomThrowable
import com.app.edonymyeon.data.datasource.preference.PreferenceDataSource
import com.app.edonymyeon.data.dto.request.NotificationPreferenceRequest
import com.app.edonymyeon.mapper.toDomain
import com.domain.edonymyeon.model.NotificationPreference
import com.domain.edonymyeon.repository.PreferenceRepository
import javax.inject.Inject

class PreferenceRepositoryImpl @Inject constructor(
    private val preferenceDataSource: PreferenceDataSource,
) : PreferenceRepository {
    override suspend fun getNotificationPreference(): Result<List<NotificationPreference>> {
        val result = preferenceDataSource.getNotificationPreference()
        return if (result.isSuccessful && result.body() != null) {
            Result.success(
                (result.body()!!.notifications).map {
                    it.toDomain()
                },
            )
        } else {
            Result.failure(CustomThrowable(result.code(), result.message()))
        }
    }

    override suspend fun saveNotificationPreference(preferenceType: String): Result<List<NotificationPreference>> {
        val result = preferenceDataSource.saveNotificationPreference(
            NotificationPreferenceRequest(preferenceType),
        )
        return if (result.isSuccessful && result.body() != null) {
            Result.success(
                (result.body()!!.notifications).map {
                    it.toDomain()
                },
            )
        } else {
            Result.failure(CustomThrowable(result.code(), result.message()))
        }
    }
}
