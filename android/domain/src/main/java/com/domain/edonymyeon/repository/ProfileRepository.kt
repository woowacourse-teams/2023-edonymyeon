package com.domain.edonymyeon.repository

import com.domain.edonymyeon.model.MyPosts
import com.domain.edonymyeon.model.Nickname
import com.domain.edonymyeon.model.Writer
import java.io.File

interface ProfileRepository {
    suspend fun getMyPosts(page: Int, notificationId: Long): Result<MyPosts>
    suspend fun postPurchaseConfirm(
        id: Long,
        purchasePrice: Int,
        year: Int,
        month: Int,
    ): Result<Unit>

    suspend fun postSavingConfirm(id: Long, year: Int, month: Int): Result<Unit>
    suspend fun deleteConfirm(id: Long): Result<Unit>
    suspend fun getProfile(): Result<Writer>
    suspend fun withdraw(): Result<Unit>

    suspend fun updateProfile(
        nickname: Nickname,
        profileImage: File?,
        isProfileChanged: Boolean,
    ): Result<Unit>

    suspend fun checkDuplicate(target: String, value: String): Result<Boolean>
}
