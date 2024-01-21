package com.app.edonymyeon.data.repository

import com.app.edonymyeon.data.datasource.preference.PreferenceDataSource
import com.app.edonymyeon.data.dto.request.NotificationPreferenceRequest
import com.app.edonymyeon.mapper.toDomain
import com.app.edonymyeon.mapper.toResult
import com.domain.edonymyeon.model.NotificationPreference
import com.domain.edonymyeon.repository.PreferenceRepository
import javax.inject.Inject

class PreferenceRepositoryImpl @Inject constructor(
    private val preferenceDataSource: PreferenceDataSource,
) : PreferenceRepository {
    override suspend fun getNotificationPreference(): Result<List<NotificationPreference>> {
        return preferenceDataSource.getNotificationPreference().toResult { it, _ ->
            it.notifications.map { notification ->
                notification.toDomain()
            }
        }
    }

    override suspend fun saveNotificationPreference(preferenceType: String): Result<List<NotificationPreference>> {
        return preferenceDataSource.saveNotificationPreference(
            NotificationPreferenceRequest(preferenceType),
        ).toResult { it, _ ->
            it.notifications.map { notification ->
                notification.toDomain()
            }
        }
    }
}
