package com.app.edonymyeon.data.datasource.preference

import com.app.edonymyeon.data.common.ApiResponse
import com.app.edonymyeon.data.dto.request.NotificationPreferenceRequest
import com.app.edonymyeon.data.dto.response.NotificationPreferenceResponse

interface PreferenceDataSource {
    suspend fun getNotificationPreference(): ApiResponse<NotificationPreferenceResponse>
    suspend fun saveNotificationPreference(notificationPreferenceRequest: NotificationPreferenceRequest):
        ApiResponse<NotificationPreferenceResponse>
}
