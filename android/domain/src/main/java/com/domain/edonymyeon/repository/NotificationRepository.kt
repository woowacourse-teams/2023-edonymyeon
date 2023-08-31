package com.domain.edonymyeon.repository

import com.domain.edonymyeon.model.Notifications

interface NotificationRepository {
    suspend fun getNotifications(size: Int, page: Int): Result<Notifications>
}
