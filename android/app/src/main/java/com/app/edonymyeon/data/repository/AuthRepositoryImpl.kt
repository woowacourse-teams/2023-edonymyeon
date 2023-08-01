package com.app.edonymyeon.data.repository

import com.app.edonymyeon.data.common.CustomThrowable
import com.app.edonymyeon.data.datasource.auth.AuthDataSource
import com.app.edonymyeon.data.datasource.user.UserDataSource
import com.app.edonymyeon.data.dto.LoginDataModel
import com.domain.edonymyeon.repository.AuthRepository
import org.json.JSONObject

class AuthRepositoryImpl(
    private val userDataSource: UserDataSource,
    private val authDataSource: AuthDataSource,
) : AuthRepository {
    override suspend fun login(email: String, password: String): Result<Unit> {
        val result = userDataSource.login(LoginDataModel(email, password))
        return if (result.isSuccessful) {
            authDataSource.setAuthToken(result.headers()["Authorization"] as String)
            Result.success(result.body() ?: Unit)
        } else {
            val errorResponse = result.errorBody()?.string()
            val json = errorResponse?.let { JSONObject(it) }
            val errorMessage = json?.getString("errorMessage") ?: ""
            Result.failure(CustomThrowable(result.code(), errorMessage))
        }
    }
}
