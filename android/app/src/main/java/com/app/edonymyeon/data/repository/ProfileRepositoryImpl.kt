package com.app.edonymyeon.data.repository

import com.app.edonymyeon.data.datasource.profile.ProfileDataSource
import com.app.edonymyeon.data.dto.request.ProfileUpdateRequest
import com.app.edonymyeon.data.dto.request.PurchaseConfirmRequest
import com.app.edonymyeon.data.dto.request.SavingConfirmRequest
import com.app.edonymyeon.mapper.toDomain
import com.app.edonymyeon.mapper.toResult
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
        return profileDataSource.getMyPosts(page, notificationId).toResult { it, _ ->
            MyPosts(
                it.posts.map { post ->
                    post.toDomain()
                },
                it.isLast,
            )
        }
    }

    override suspend fun postPurchaseConfirm(
        id: Long,
        purchasePrice: Int,
        year: Int,
        month: Int,
    ): Result<Unit> {
        return profileDataSource.postPurchaseConfirm(
            id,
            PurchaseConfirmRequest(purchasePrice, year, month),
        ).toResult()
    }

    override suspend fun postSavingConfirm(id: Long, year: Int, month: Int): Result<Unit> {
        return profileDataSource.postSavingConfirm(id, SavingConfirmRequest(year, month)).toResult()
    }

    override suspend fun deleteConfirm(id: Long): Result<Unit> {
        return profileDataSource.deleteConfirm(id).toResult()
    }

    override suspend fun getProfile(): Result<Writer> {
        return profileDataSource.getProfile().toResult { it, _ ->
            it.toDomain()
        }
    }

    override suspend fun withdraw(): Result<Unit> {
        return profileDataSource.withdraw().toResult()
    }

    override suspend fun updateProfile(
        nickname: Nickname,
        profileImage: File?,
        isProfileChanged: Boolean,
    ): Result<Unit> {
        return profileDataSource.updateProfile(
            ProfileUpdateRequest(
                nickname.value,
                isProfileChanged,
            ),
            profileImage,
        ).toResult()
    }

    override suspend fun checkDuplicate(
        target: String,
        value: String,
    ): Result<Boolean> {
        return profileDataSource.checkDuplicate(target, value).toResult()
    }
}
