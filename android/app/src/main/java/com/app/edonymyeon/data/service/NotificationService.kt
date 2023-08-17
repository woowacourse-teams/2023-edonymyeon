package com.app.edonymyeon.data.service

import com.app.edonymyeon.data.dto.response.NotificationsResponse
import retrofit2.Response
import retrofit2.http.GET

interface NotificationService {
    @GET("/notifications")
    fun getNotifications(): Response<NotificationsResponse>
}
