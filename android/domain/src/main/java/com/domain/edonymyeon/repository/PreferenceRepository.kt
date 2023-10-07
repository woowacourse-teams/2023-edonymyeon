package com.domain.edonymyeon.repository

import com.domain.edonymyeon.model.NotificationPreference

interface PreferenceRepository {
    suspend fun getNotificationPreference(): Result<List<NotificationPreference>>
    suspend fun saveNotificationPreference(preferenceType: String): Result<List<NotificationPreference>>
}
