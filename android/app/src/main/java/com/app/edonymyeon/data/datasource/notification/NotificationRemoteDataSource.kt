package com.app.edonymyeon.data.datasource.notification

import com.app.edonymyeon.data.dto.response.NotificationsResponse
import com.app.edonymyeon.data.service.NotificationService
import com.app.edonymyeon.data.service.client.calladapter.ApiResponse
import javax.inject.Inject

class NotificationRemoteDataSource @Inject constructor(
    private val notificationService: NotificationService,
) : NotificationDataSource {

    override suspend fun getNotifications(
        size: Int,
        page: Int,
    ): ApiResponse<NotificationsResponse> {
        return notificationService.getNotifications(size, page)
    }
}
