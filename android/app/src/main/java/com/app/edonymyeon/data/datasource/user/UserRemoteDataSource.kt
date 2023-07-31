package com.app.edonymyeon.data.datasource.user

import com.app.edonymyeon.data.dto.LoginDataModel
import com.app.edonymyeon.data.service.AuthService
import com.app.edonymyeon.data.service.client.RetrofitClient
import retrofit2.Response

class UserRemoteDataSource : UserDataSource {

    private val authService: AuthService =
        RetrofitClient.getInstance().create(AuthService::class.java)

    override suspend fun login(loginDataModel: LoginDataModel): Response<Unit> {
        return authService.login(loginDataModel)
    }
}
