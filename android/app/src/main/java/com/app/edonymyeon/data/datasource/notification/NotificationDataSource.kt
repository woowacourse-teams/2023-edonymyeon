package com.app.edonymyeon.data.datasource.notification

import com.app.edonymyeon.data.dto.response.NotificationsResponse
import retrofit2.Response

interface NotificationDataSource {
    suspend fun getNotifications(size: Int, page: Int): Response<NotificationsResponse>
}
