package com.app.edonymyeon.data.repository

import com.app.edonymyeon.data.common.CustomThrowable
import com.app.edonymyeon.data.datasource.auth.AuthDataSource
import com.app.edonymyeon.data.dto.response.AuthDuplicateResponse
import com.app.edonymyeon.mapper.toDataModel
import com.domain.edonymyeon.model.UserRegistration
import com.domain.edonymyeon.repository.AuthRepository

class AuthRepositoryImpl(private val authDataSource: AuthDataSource) : AuthRepository {
    override suspend fun signUp(userRegistration: UserRegistration): Result<Unit> {
        val result = authDataSource.signUp(
            userRegistration.toDataModel(),
        )
        return if (result.isSuccessful) {
            Result.success(Unit)
        } else {
            Result.failure(CustomThrowable(result.code(), result.message()))
        }
    }

    override suspend fun checkDuplicate(
        target: String,
        value: String,
    ): Result<Boolean> {
        val result = authDataSource.checkDuplicate(target, value)
        return if (result.isSuccessful) {
            Result.success((result.body() ?: AuthDuplicateResponse(false)).isUnique)
        } else {
            Result.failure(CustomThrowable(result.code(), result.message()))
        }
    }
}
