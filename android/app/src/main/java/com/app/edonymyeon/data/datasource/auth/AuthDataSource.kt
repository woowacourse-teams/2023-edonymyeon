package com.app.edonymyeon.data.datasource.auth

import com.app.edonymyeon.data.dto.request.UserRegistrationRequestBody
import com.app.edonymyeon.data.dto.response.AuthDuplicateResponse
import retrofit2.Response

interface AuthDataSource {
    suspend fun signUp(userRegistrationRequestBody: UserRegistrationRequestBody): Response<Unit>
    suspend fun checkDuplicate(target: String, value: String): Response<AuthDuplicateResponse>
}
