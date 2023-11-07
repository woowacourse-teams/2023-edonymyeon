package com.app.edonymyeon.data.datasource.search

import com.app.edonymyeon.data.common.ApiResponse
import com.app.edonymyeon.data.dto.response.Posts

interface SearchDataSource {
    suspend fun getSearchResult(query: String, page: Int): ApiResponse<Posts>
}
