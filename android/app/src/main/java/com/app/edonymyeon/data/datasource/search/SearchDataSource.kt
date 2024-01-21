package com.app.edonymyeon.data.datasource.search

import com.app.edonymyeon.data.dto.response.Posts
import com.app.edonymyeon.data.service.client.calladapter.ApiResponse

interface SearchDataSource {
    suspend fun getSearchResult(query: String, page: Int): ApiResponse<Posts>
}
