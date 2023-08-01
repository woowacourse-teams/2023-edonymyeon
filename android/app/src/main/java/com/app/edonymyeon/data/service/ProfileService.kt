package com.app.edonymyeon.data.service

import com.app.edonymyeon.data.dto.response.MyPostsResponse
import retrofit2.Response
import retrofit2.http.GET
import retrofit2.http.Query

interface ProfileService {

    @GET("/profile/myPosts")
    fun getMyPost(
        @Query("size") size: Int,
        @Query("page") page: Int,
    ): Response<MyPostsResponse>
}
