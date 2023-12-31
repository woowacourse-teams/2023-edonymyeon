package com.app.edonymyeon.data.datasource.preference

import com.app.edonymyeon.data.dto.request.NotificationPreferenceRequest
import com.app.edonymyeon.data.dto.response.NotificationPreferenceResponse
import com.app.edonymyeon.data.service.PreferenceService
import retrofit2.Response
import javax.inject.Inject

class PreferenceRemoteDataSource @Inject constructor(
    private val preferenceService: PreferenceService,
) : PreferenceDataSource {

    override suspend fun getNotificationPreference(): Response<NotificationPreferenceResponse> {
        return preferenceService.getNotificationPreference()
    }

    override suspend fun saveNotificationPreference(notificationPreferenceRequest: NotificationPreferenceRequest): Response<NotificationPreferenceResponse> {
        return preferenceService.saveNotificationPreference(notificationPreferenceRequest)
    }
}
