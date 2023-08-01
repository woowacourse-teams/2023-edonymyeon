package com.app.edonymyeon.data.datasource.profile

import com.app.edonymyeon.data.dto.response.MyPostsResponse
import retrofit2.Response

interface ProfileDataSource {
    suspend fun getMyPosts(size: Int, page: Int): Response<MyPostsResponse>
}
