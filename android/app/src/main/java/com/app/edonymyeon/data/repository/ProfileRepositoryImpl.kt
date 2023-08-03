package com.app.edonymyeon.data.repository

import com.app.edonymyeon.data.common.CustomThrowable
import com.app.edonymyeon.data.datasource.profile.ProfileDataSource
import com.app.edonymyeon.data.dto.response.ProfileResponse
import com.app.edonymyeon.mapper.toDomain
import com.domain.edonymyeon.repository.ProfileRepository

class ProfileRepositoryImpl(private val profileDataSource: ProfileDataSource) : ProfileRepository {
    override suspend fun getProfile(): Result<Any> {
        val result = profileDataSource.getProfile()
        return if (result.isSuccessful) {
            Result.success((result.body() as ProfileResponse).toDomain())
        } else {
            Result.failure(CustomThrowable(result.code(), result.message()))
        }
    }
}
