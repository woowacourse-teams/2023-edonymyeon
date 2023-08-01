package com.domain.edonymyeon.repository

import com.domain.edonymyeon.model.MyPost

interface ProfileRepository {

    suspend fun getMyPosts(size: Int, page: Int): Result<List<MyPost>>
    suspend fun postPurchaseConfirm(id: Long, purchasePrice: Int, year: Int, month: Int): Result<Unit>
    suspend fun postSavingConfirm(id: Long, year: Int, month: Int): Result<Unit>
    suspend fun deleteConfirm(id: Long): Result<Unit>
}
