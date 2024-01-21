package com.app.edonymyeon.data.repository

import com.app.edonymyeon.data.datasource.notification.NotificationDataSource
import com.app.edonymyeon.mapper.toDomain
import com.app.edonymyeon.mapper.toResult
import com.domain.edonymyeon.model.Notifications
import com.domain.edonymyeon.repository.NotificationRepository
import javax.inject.Inject

class NotificationRepositoryImpl @Inject constructor(private val notificationDataSource: NotificationDataSource) :
    NotificationRepository {
    override suspend fun getNotifications(size: Int, page: Int): Result<Notifications> {
        return notificationDataSource.getNotifications(size, page).toResult { it, _ ->
            it.toDomain()
        }
    }
}
