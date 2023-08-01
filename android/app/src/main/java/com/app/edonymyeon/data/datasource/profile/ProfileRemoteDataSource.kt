package com.app.edonymyeon.data.datasource.profile

import com.app.edonymyeon.data.dto.response.MyPostsResponse
import com.app.edonymyeon.data.service.ProfileService
import com.app.edonymyeon.data.service.client.RetrofitClient
import retrofit2.Response

class ProfileRemoteDataSource : ProfileDataSource {

    private val profileService: ProfileService =
        RetrofitClient.getInstance().create(ProfileService::class.java)

    init {
        RetrofitClient.getInstance()
            .updateAccessToken("Basic YmVhdXRpZnVsbmVvQG5hdmVyLmNvbTpuZW8xMjM=")
    }

    override suspend fun getMyPosts(size: Int, page: Int): Response<MyPostsResponse> {
        return profileService.getMyPost(size, page)
    }
}
