package com.app.edonymyeon.data.datasource.user

import com.app.edonymyeon.data.dto.LoginDataModel
import retrofit2.Response

interface UserDataSource {

    suspend fun login(
        loginDataModel: LoginDataModel,
    ): Response<Unit>
}
