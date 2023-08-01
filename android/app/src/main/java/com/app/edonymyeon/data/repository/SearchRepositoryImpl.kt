package com.app.edonymyeon.data.repository

import com.app.edonymyeon.data.common.CustomThrowable
import com.app.edonymyeon.data.datasource.search.SearchDataSource
import com.app.edonymyeon.data.dto.response.Posts
import com.app.edonymyeon.mapper.toDomain
import com.domain.edonymyeon.model.PostItem
import com.domain.edonymyeon.repository.SearchRepository

class SearchRepositoryImpl(private val searchDataSource: SearchDataSource) : SearchRepository {
    override suspend fun getSearchResult(query: String, page: Int): Result<List<PostItem>> {
        val result = searchDataSource.getSearchResult(query, page)
        return if (result.isSuccessful) {
            Result.success(((result.body() as Posts).post).map { it.toDomain() })
        } else {
            Result.failure(CustomThrowable(result.code(), result.message()))
        }
    }
}
