package com.app.edonymyeon.data.service

import com.app.edonymyeon.data.dto.response.Posts
import com.app.edonymyeon.data.service.client.calladapter.ApiResponse
import retrofit2.http.GET
import retrofit2.http.Query

interface SearchService {
    @GET("/search")
    suspend fun getSearchResult(
        @Query("query") query: String,
        @Query("size") size: Int,
        @Query("page") page: Int,
    ): ApiResponse<Posts>
}
