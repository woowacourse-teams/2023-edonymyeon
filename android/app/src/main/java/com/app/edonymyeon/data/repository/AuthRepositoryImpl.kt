package com.app.edonymyeon.data.repository

import com.app.edonymyeon.data.datasource.auth.AuthDataSource
import com.app.edonymyeon.data.dto.LoginDataModel
import com.app.edonymyeon.data.dto.request.LogoutRequest
import com.app.edonymyeon.data.dto.request.TokenRequest
import com.app.edonymyeon.mapper.toDataModel
import com.app.edonymyeon.mapper.toResult
import com.domain.edonymyeon.model.UserRegistration
import com.domain.edonymyeon.repository.AuthRepository
import javax.inject.Inject

class AuthRepositoryImpl @Inject constructor(
    private val authLocalDataSource: AuthDataSource.Local,
    private val authRemoteDataSource: AuthDataSource.Remote,
) : AuthRepository {

    override fun getToken(): String? {
        return authLocalDataSource.getAuthToken()
    }

    override fun setToken(token: String?) {
        authLocalDataSource.setAuthToken(token)
    }

    override suspend fun signUp(userRegistration: UserRegistration): Result<Unit> {
        return authRemoteDataSource.signUp(
            userRegistration.toDataModel(),
        ).toResult()
    }

    override suspend fun checkDuplicate(
        target: String,
        value: String,
    ): Result<Boolean> {
        return authRemoteDataSource.checkDuplicate(target, value).toResult { it, _ ->
            it.isUnique
        }
    }

    override suspend fun login(email: String, password: String, deviceToken: String): Result<Unit> {
        return authRemoteDataSource.login(LoginDataModel(email, password, deviceToken))
            .toResult { _, headers ->
                authLocalDataSource.setAuthToken(headers["Set-Cookie"] as String)
            }
    }

    override suspend fun loginByKakao(accessToken: String, deviceToken: String): Result<Unit> {
        return authRemoteDataSource.loginByKakao(TokenRequest(accessToken, deviceToken))
            .toResult { _, headers ->
                authLocalDataSource.setAuthToken(headers["Set-Cookie"] as String)
            }
    }

    override suspend fun logout(deviceToken: String): Result<Unit> {
        return authRemoteDataSource.logout(LogoutRequest(deviceToken)).toResult { _, _ ->
            authLocalDataSource.setAuthToken(null)
        }
    }
}
