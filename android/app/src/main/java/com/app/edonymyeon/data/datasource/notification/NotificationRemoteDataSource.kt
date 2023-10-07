package com.app.edonymyeon.data.datasource.notification

import com.app.edonymyeon.data.dto.response.NotificationsResponse
import com.app.edonymyeon.data.service.NotificationService
import retrofit2.Response
import javax.inject.Inject

class NotificationRemoteDataSource @Inject constructor(
    private val notificationService: NotificationService,
) : NotificationDataSource {

    override suspend fun getNotifications(size: Int, page: Int): Response<NotificationsResponse> {
        return notificationService.getNotifications(size, page)
    }
}
