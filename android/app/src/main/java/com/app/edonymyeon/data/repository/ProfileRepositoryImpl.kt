package com.app.edonymyeon.data.repository

import com.app.edonymyeon.data.common.CustomThrowable
import com.app.edonymyeon.data.datasource.profile.ProfileDataSource
import com.app.edonymyeon.data.dto.request.PurchaseConfirmRequest
import com.app.edonymyeon.data.dto.request.SavingConfirmRequest
import com.app.edonymyeon.data.dto.response.MyPostsResponse
import com.app.edonymyeon.mapper.toDomain
import com.domain.edonymyeon.model.MyPost
import com.domain.edonymyeon.repository.ProfileRepository

class ProfileRepositoryImpl(private val profileDataSource: ProfileDataSource) : ProfileRepository {
    override suspend fun getMyPosts(size: Int, page: Int): Result<List<MyPost>> {
        val result = profileDataSource.getMyPosts(size, page)
        return if (result.isSuccessful) {
            Result.success((result.body() as MyPostsResponse).posts.map { it.toDomain() })
        } else {
            Result.failure(CustomThrowable(result.code(), result.message()))
        }
    }

    override suspend fun postPurchaseConfirm(
        id: Long,
        purchasePrice: Int,
        year: Int,
        month: Int,
    ): Result<Unit> {
        val result = profileDataSource.postPurchaseConfirm(
            id,
            PurchaseConfirmRequest(purchasePrice, year, month),
        )
        return if (result.isSuccessful) {
            Result.success((result.body() ?: Unit))
        } else {
            Result.failure(CustomThrowable(result.code(), result.message()))
        }
    }

    override suspend fun postSavingConfirm(id: Long, year: Int, month: Int): Result<Unit> {
        val result = profileDataSource.postSavingConfirm(id, SavingConfirmRequest(year, month))
        return if (result.isSuccessful) {
            Result.success((result.body() ?: Unit))
        } else {
            Result.failure(CustomThrowable(result.code(), result.message()))
        }
    }

    override suspend fun deleteConfirm(id: Long): Result<Unit> {
        val result = profileDataSource.deleteConfirm(id)
        return if (result.isSuccessful) {
            Result.success((result.body() ?: Unit))
        } else {
            Result.failure(CustomThrowable(result.code(), result.message()))
        }
    }
}
