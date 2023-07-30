package com.app.edonymyeon.data.service

import com.app.edonymyeon.data.dto.LoginDataModel
import retrofit2.Response
import retrofit2.http.Body
import retrofit2.http.POST

interface UserService {

    @POST("/login")
    suspend fun login(
        @Body loginDataModel: LoginDataModel,
    ): Response<Unit>
}
