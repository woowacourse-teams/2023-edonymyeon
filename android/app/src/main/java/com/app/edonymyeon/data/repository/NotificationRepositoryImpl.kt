package com.app.edonymyeon.data.repository

import com.app.edonymyeon.data.common.CustomThrowable
import com.app.edonymyeon.data.datasource.notification.NotificationDataSource
import com.app.edonymyeon.mapper.toDomain
import com.domain.edonymyeon.model.Notifications
import com.domain.edonymyeon.repository.NotificationRepository

class NotificationRepositoryImpl(private val notificationDataSource: NotificationDataSource) :
    NotificationRepository {
    override suspend fun getNotifications(size: Int, page: Int): Result<Notifications> {
        val result = notificationDataSource.getNotifications(size, page)
        return if (result.isSuccessful && result.body() != null) {
            Result.success(result.body()!!.toDomain())
        } else {
            Result.failure(CustomThrowable(result.code(), result.message()))
        }
    }
}