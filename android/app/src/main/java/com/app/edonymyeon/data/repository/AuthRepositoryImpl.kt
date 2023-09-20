package com.app.edonymyeon.data.repository

import com.app.edonymyeon.data.common.CustomThrowable
import com.app.edonymyeon.data.common.createCustomThrowableFromResponse
import com.app.edonymyeon.data.datasource.auth.AuthDataSource
import com.app.edonymyeon.data.dto.LoginDataModel
import com.app.edonymyeon.data.dto.request.LogoutRequest
import com.app.edonymyeon.data.dto.request.TokenRequest
import com.app.edonymyeon.data.dto.response.AuthDuplicateResponse
import com.app.edonymyeon.mapper.toDataModel
import com.domain.edonymyeon.model.UserRegistration
import com.domain.edonymyeon.repository.AuthRepository
import org.json.JSONObject
import javax.inject.Inject

class AuthRepositoryImpl @Inject constructor(
    private val authLocalDataSource: AuthDataSource.Local,
    private val authRemoteDataSource: AuthDataSource.Remote,
) : AuthRepository {
    override suspend fun signUp(userRegistration: UserRegistration): Result<Unit> {
        val result = authRemoteDataSource.signUp(
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
        val result = authRemoteDataSource.checkDuplicate(target, value)

        return if (result.isSuccessful) {
            Result.success((result.body() ?: AuthDuplicateResponse(false)).isUnique)
        } else {
            Result.failure(CustomThrowable(result.code(), result.message()))
        }
    }

    override suspend fun login(email: String, password: String, deviceToken: String): Result<Unit> {
        val result = authRemoteDataSource.login(LoginDataModel(email, password, deviceToken))

        return if (result.isSuccessful) {
            authLocalDataSource.setAuthToken(result.headers()["Set-Cookie"] as String)
            Result.success(result.body() ?: Unit)
        } else {
            val customThrowable = createCustomThrowableFromResponse(result)
            Result.failure(customThrowable)
        }
    }

    override suspend fun loginByKakao(accessToken: String, deviceToken: String): Result<Unit> {
        val result = authRemoteDataSource.loginByKakao(TokenRequest(accessToken, deviceToken))

        return if (result.isSuccessful) {
            authLocalDataSource.setAuthToken(result.headers()["Set-Cookie"] as String)
            Result.success(result.body() ?: Unit)
        } else {
            val errorResponse = result.errorBody()?.string()
            val json = errorResponse?.let { JSONObject(it) }
            val errorMessage = json?.getString("errorMessage") ?: ""
            Result.failure(CustomThrowable(result.code(), errorMessage))
        }
    }

    override suspend fun logout(deviceToken: String): Result<Unit> {
        val result = authRemoteDataSource.logout(LogoutRequest(deviceToken))
        return if (result.isSuccessful) {
            authLocalDataSource.setAuthToken("")
            Result.success(Unit)
        } else {
            Result.failure(CustomThrowable(result.code(), result.message()))
        }
    }
}
