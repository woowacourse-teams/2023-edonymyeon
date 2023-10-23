package com.app.edonymyeon.data.datasource.preference

import com.app.edonymyeon.data.dto.request.NotificationPreferenceRequest
import com.app.edonymyeon.data.dto.response.NotificationPreferenceResponse
import retrofit2.Response

interface PreferenceDataSource {
    suspend fun getNotificationPreference(): Response<NotificationPreferenceResponse>
    suspend fun saveNotificationPreference(notificationPreferenceRequest: NotificationPreferenceRequest):
            Response<NotificationPreferenceResponse>
}
