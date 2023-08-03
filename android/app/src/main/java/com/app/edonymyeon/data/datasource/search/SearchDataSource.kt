package com.app.edonymyeon.data.datasource.search

import com.app.edonymyeon.data.dto.response.Posts
import retrofit2.Response

interface SearchDataSource {
    suspend fun getSearchResult(query: String, page: Int): Response<Posts>
}
