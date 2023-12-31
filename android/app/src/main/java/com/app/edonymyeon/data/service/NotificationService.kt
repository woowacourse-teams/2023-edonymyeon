package com.app.edonymyeon.data.service

import com.app.edonymyeon.data.dto.response.NotificationsResponse
import retrofit2.Response
import retrofit2.http.GET
import retrofit2.http.Query

interface NotificationService {
    @GET("/notification")
    suspend fun getNotifications(
        @Query("size") size: Int,
        @Query("page") page: Int,
    ): Response<NotificationsResponse>
}
