package com.app.edonymyeon.data.datasource.notification

import com.app.edonymyeon.data.common.ApiResponse
import com.app.edonymyeon.data.dto.response.NotificationsResponse

interface NotificationDataSource {
    suspend fun getNotifications(size: Int, page: Int): ApiResponse<NotificationsResponse>
}
