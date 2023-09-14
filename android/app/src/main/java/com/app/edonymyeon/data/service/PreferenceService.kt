package com.app.edonymyeon.data.service

import com.app.edonymyeon.data.dto.request.NotificationPreferenceRequest
import com.app.edonymyeon.data.dto.response.NotificationPreferenceResponse
import retrofit2.Response
import retrofit2.http.Body
import retrofit2.http.GET
import retrofit2.http.POST

interface PreferenceService {
    @GET("/preference/notification")
    suspend fun getNotificationPreference(): Response<NotificationPreferenceResponse>

    @POST("/preference/notification")
    suspend fun saveNotificationPreference(
        @Body notificationPreferenceRequest: NotificationPreferenceRequest,
    ): Response<NotificationPreferenceResponse>
}
