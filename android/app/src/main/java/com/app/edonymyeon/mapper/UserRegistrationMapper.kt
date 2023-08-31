package com.app.edonymyeon.mapper

import com.app.edonymyeon.data.dto.request.UserRegistrationRequest
import com.domain.edonymyeon.model.UserRegistration

fun UserRegistration.toDataModel(): UserRegistrationRequest {
    return UserRegistrationRequest(
        email = email.value,
        password = password.value,
        nickname = nickname.value,
        deviceToken = deviceToken,
    )
}
