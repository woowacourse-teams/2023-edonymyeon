package com.domain.edonymyeon.repository

import com.domain.edonymyeon.model.MyPosts

interface ProfileRepository {
    suspend fun getMyPosts(page: Int): Result<MyPosts>
    suspend fun postPurchaseConfirm(
        id: Long,
        purchasePrice: Int,
        year: Int,
        month: Int,
    ): Result<Unit>

    suspend fun postSavingConfirm(id: Long, year: Int, month: Int): Result<Unit>
    suspend fun deleteConfirm(id: Long): Result<Unit>
    suspend fun getProfile(): Result<Any>
}
