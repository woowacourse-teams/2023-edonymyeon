package com.app.edonymyeon.data.repository

import com.app.edonymyeon.data.common.CustomThrowable
import com.app.edonymyeon.data.datasource.profile.ProfileDataSource
import com.app.edonymyeon.data.dto.response.ProfileResponse
import com.app.edonymyeon.mapper.toDomain
import com.domain.edonymyeon.repository.ProfileRepository
import org.json.JSONObject

class ProfileRepositoryImpl(private val profileDataSource: ProfileDataSource) : ProfileRepository {
    override suspend fun getProfile(): Result<Any> {
        val result = profileDataSource.getProfile()
        return if (result.isSuccessful) {
            Result.success((result.body() as ProfileResponse).toDomain())
        } else {
            val errorResponse = result.errorBody()?.string()
            val json = errorResponse?.let { JSONObject(it) }
            val errorMessage = json?.getString("errorMessage") ?: ""
            Result.failure(CustomThrowable(result.code(), errorMessage))
        }
    }
}
