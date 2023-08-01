package com.domain.edonymyeon.repository

import com.domain.edonymyeon.model.MyPost

interface ProfileRepository {
    suspend fun getMyPosts(size: Int, page: Int): Result<List<MyPost>>
}
