package com.app.edonymyeon.data.datasource.notification

import com.app.edonymyeon.data.dto.response.NotificationsResponse
import com.app.edonymyeon.data.service.client.calladapter.ApiResponse

interface NotificationDataSource {
    suspend fun getNotifications(size: Int, page: Int): ApiResponse<NotificationsResponse>
}
