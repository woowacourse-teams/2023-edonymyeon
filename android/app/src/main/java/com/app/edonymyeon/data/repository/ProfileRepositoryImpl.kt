package com.app.edonymyeon.data.repository

import com.app.edonymyeon.data.common.createCustomThrowableFromResponse
import com.app.edonymyeon.data.datasource.profile.ProfileDataSource
import com.app.edonymyeon.data.dto.WriterDataModel
import com.app.edonymyeon.data.dto.request.ProfileUpdateRequest
import com.app.edonymyeon.data.dto.request.PurchaseConfirmRequest
import com.app.edonymyeon.data.dto.request.SavingConfirmRequest
import com.app.edonymyeon.data.dto.response.AuthDuplicateResponse
import com.app.edonymyeon.data.dto.response.MyPostsResponse
import com.app.edonymyeon.mapper.toDomain
import com.domain.edonymyeon.model.MyPosts
import com.domain.edonymyeon.model.Nickname
import com.domain.edonymyeon.model.Writer
import com.domain.edonymyeon.repository.ProfileRepository
import java.io.File
import javax.inject.Inject

class ProfileRepositoryImpl @Inject constructor(
    private val profileDataSource: ProfileDataSource,
) : ProfileRepository {
    override suspend fun getMyPosts(page: Int, notificationId: Long): Result<MyPosts> {
        val result = profileDataSource.getMyPosts(page, notificationId)
        return if (result.isSuccessful) {
            Result.success(
                MyPosts(
                    (result.body() as MyPostsResponse).posts.map { it.toDomain() },
                    result.body()!!.isLast,
                ),
            )
        } else {
            val customThrowable = createCustomThrowableFromResponse(result)
            Result.failure(customThrowable)
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
            val customThrowable = createCustomThrowableFromResponse(result)
            Result.failure(customThrowable)
        }
    }

    override suspend fun postSavingConfirm(id: Long, year: Int, month: Int): Result<Unit> {
        val result = profileDataSource.postSavingConfirm(id, SavingConfirmRequest(year, month))
        return if (result.isSuccessful) {
            Result.success((result.body() ?: Unit))
        } else {
            val customThrowable = createCustomThrowableFromResponse(result)
            Result.failure(customThrowable)
        }
    }

    override suspend fun deleteConfirm(id: Long): Result<Unit> {
        val result = profileDataSource.deleteConfirm(id)
        return if (result.isSuccessful) {
            Result.success((result.body() ?: Unit))
        } else {
            val customThrowable = createCustomThrowableFromResponse(result)
            Result.failure(customThrowable)
        }
    }

    override suspend fun getProfile(): Result<Writer> {
        val result = profileDataSource.getProfile()
        return if (result.isSuccessful) {
            Result.success((result.body() as WriterDataModel).toDomain())
        } else {
            val customThrowable = createCustomThrowableFromResponse(result)
            Result.failure(customThrowable)
        }
    }

    override suspend fun withdraw(): Result<Unit> {
        val result = profileDataSource.withdraw()
        return if (result.isSuccessful) {
            Result.success(result.body() ?: Unit)
        } else {
            val customThrowable = createCustomThrowableFromResponse(result)
            Result.failure(customThrowable)
        }
    }

    override suspend fun updateProfile(
        nickname: Nickname,
        profileImage: File?,
        isProfileChanged: Boolean,
    ): Result<Unit> {
        val result = profileDataSource.updateProfile(
            ProfileUpdateRequest(
                nickname.value,
                isProfileChanged,
            ),
            profileImage,
        )
        return if (result.isSuccessful) {
            Result.success(result.body() ?: Unit)
        } else {
            val customThrowable = createCustomThrowableFromResponse(result)
            Result.failure(customThrowable)
        }
    }

    override suspend fun checkDuplicate(
        target: String,
        value: String,
    ): Result<Boolean> {
        val result = profileDataSource.checkDuplicate(target, value)

        return if (result.isSuccessful) {
            Result.success((result.body() ?: AuthDuplicateResponse(false)).isUnique)
        } else {
            val customThrowable = createCustomThrowableFromResponse(result)
            Result.failure(customThrowable)
        }
    }
}
