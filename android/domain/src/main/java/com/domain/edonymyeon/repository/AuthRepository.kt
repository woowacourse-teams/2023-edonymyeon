package com.domain.edonymyeon.repository

import com.domain.edonymyeon.model.UserRegistration

interface AuthRepository {
    suspend fun signUp(userRegistration: UserRegistration): Result<Unit>

    suspend fun checkDuplicate(target: String, value: String): Result<Boolean>
}
