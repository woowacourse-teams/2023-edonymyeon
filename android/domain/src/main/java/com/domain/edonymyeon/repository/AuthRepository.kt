package com.domain.edonymyeon.repository

import com.domain.edonymyeon.model.UserRegistration

interface AuthRepository {
    suspend fun login(email: String, password: String, deviceToken: String): Result<Unit>

    suspend fun loginByKakao(accessToken: String, deviceToken: String): Result<Unit>

    suspend fun signUp(userRegistration: UserRegistration): Result<Unit>

    suspend fun checkDuplicate(target: String, value: String): Result<Boolean>

    suspend fun logout(deviceToken: String): Result<Unit>
}
