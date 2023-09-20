package com.app.edonymyeon.data.datasource.search

import com.app.edonymyeon.data.dto.response.Posts
import com.app.edonymyeon.data.service.SearchService
import com.app.edonymyeon.data.service.client.RetrofitClient
import retrofit2.Response
import javax.inject.Inject

class SearchRemoteDataSource @Inject constructor() : SearchDataSource {
    private val searchService: SearchService =
        RetrofitClient.getInstance().create(SearchService::class.java)

    override suspend fun getSearchResult(query: String, page: Int): Response<Posts> {
        return searchService.getSearchResult(query, RESULT_LOADING_SIZE, page)
    }

    companion object {
        private const val RESULT_LOADING_SIZE = 20
    }
}
