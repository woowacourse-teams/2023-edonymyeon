package com.app.edonymyeon.mapper

import com.app.edonymyeon.data.dto.request.UserRegistrationRequestBody
import com.domain.edonymyeon.model.UserRegistration

fun UserRegistration.toDataModel(): UserRegistrationRequestBody {
    return UserRegistrationRequestBody(
        email = email.value,
        password = password.value,
        nickname = nickname.value,
    )
}
