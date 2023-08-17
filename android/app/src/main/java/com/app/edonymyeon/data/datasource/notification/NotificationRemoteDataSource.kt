package com.app.edonymyeon.data.datasource.notification

import com.app.edonymyeon.data.dto.response.NotificationsResponse
import com.app.edonymyeon.data.service.NotificationService
import com.app.edonymyeon.data.service.client.RetrofitClient
import retrofit2.Response

class NotificationRemoteDataSource : NotificationDataSource {
    private val notificationService: NotificationService =
        RetrofitClient.getInstance().create(NotificationService::class.java)

    override fun getNotifications(): Response<NotificationsResponse> {
        return notificationService.getNotifications()
    }
}
