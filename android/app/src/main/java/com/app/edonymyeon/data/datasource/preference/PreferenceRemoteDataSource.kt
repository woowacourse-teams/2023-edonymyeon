package com.app.edonymyeon.data.datasource.preference

import com.app.edonymyeon.data.dto.request.NotificationPreferenceRequest
import com.app.edonymyeon.data.dto.response.NotificationPreferenceResponse
import com.app.edonymyeon.data.service.PreferenceService
import com.app.edonymyeon.data.service.client.RetrofitClient
import retrofit2.Response

class PreferenceRemoteDataSource : PreferenceDataSource {
    private val preferenceService =
        RetrofitClient.getInstance().create(PreferenceService::class.java)

    override suspend fun getNotificationPreference(): Response<NotificationPreferenceResponse> {
        return preferenceService.getNotificationPreference()
    }

    override suspend fun saveNotificationPreference(notificationPreferenceRequest: NotificationPreferenceRequest): Response<NotificationPreferenceResponse> {
        return preferenceService.saveNotificationPreference(notificationPreferenceRequest)
    }
}
