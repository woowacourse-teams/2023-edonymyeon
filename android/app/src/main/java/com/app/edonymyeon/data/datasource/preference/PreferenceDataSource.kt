package com.app.edonymyeon.data.datasource.preference

import com.app.edonymyeon.data.dto.request.NotificationPreferenceRequest
import com.app.edonymyeon.data.dto.response.NotificationPreferenceResponse
import com.app.edonymyeon.data.service.client.calladapter.ApiResponse

interface PreferenceDataSource {
    suspend fun getNotificationPreference(): ApiResponse<NotificationPreferenceResponse>
    suspend fun saveNotificationPreference(notificationPreferenceRequest: NotificationPreferenceRequest):
        ApiResponse<NotificationPreferenceResponse>
}
