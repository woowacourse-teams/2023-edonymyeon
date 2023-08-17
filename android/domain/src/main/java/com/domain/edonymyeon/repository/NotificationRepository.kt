package com.domain.edonymyeon.repository

import com.domain.edonymyeon.model.Notifications

interface NotificationRepository {
    fun getNotifications(): Result<Notifications>
}
