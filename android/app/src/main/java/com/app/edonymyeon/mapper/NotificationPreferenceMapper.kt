package com.app.edonymyeon.mapper

import com.app.edonymyeon.data.dto.NotificationPreferenceDataModel
import com.domain.edonymyeon.model.NotificationPreference

fun NotificationPreferenceDataModel.toDomain() = NotificationPreference(
    preferenceType = preferenceType,
    enabled = enabled,
)
