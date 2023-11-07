package com.app.edonymyeon.data.datasource.search

import com.app.edonymyeon.data.common.ApiResponse
import com.app.edonymyeon.data.dto.response.Posts
import com.app.edonymyeon.data.service.SearchService
import javax.inject.Inject

class SearchRemoteDataSource @Inject constructor(
    private val searchService: SearchService,
) : SearchDataSource {

    override suspend fun getSearchResult(query: String, page: Int): ApiResponse<Posts> {
        return searchService.getSearchResult(query, RESULT_LOADING_SIZE, page)
    }

    companion object {
        private const val RESULT_LOADING_SIZE = 20
    }
}
