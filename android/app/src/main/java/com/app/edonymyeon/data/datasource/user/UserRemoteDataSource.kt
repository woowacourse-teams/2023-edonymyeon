package com.app.edonymyeon.data.datasource.user

import com.app.edonymyeon.data.dto.LoginDataModel
import com.app.edonymyeon.data.service.UserService
import com.app.edonymyeon.data.service.client.RetrofitClient
import retrofit2.Response

class UserRemoteDataSource : UserDataSource {

    private val userService: UserService =
        RetrofitClient.getInstance().create(UserService::class.java)

    override suspend fun login(loginDataModel: LoginDataModel): Response<Unit> {
        return userService.login(loginDataModel)
    }
}
