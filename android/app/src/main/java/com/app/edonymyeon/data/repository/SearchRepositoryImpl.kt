package com.app.edonymyeon.data.repository

import com.app.edonymyeon.data.common.CustomThrowable
import com.app.edonymyeon.data.datasource.search.SearchDataSource
import com.app.edonymyeon.mapper.toDomain
import com.domain.edonymyeon.model.PostItems
import com.domain.edonymyeon.repository.SearchRepository

class SearchRepositoryImpl(private val searchDataSource: SearchDataSource) : SearchRepository {
    override suspend fun getSearchResult(query: String, page: Int): Result<PostItems> {
        val result = searchDataSource.getSearchResult(query, page)
        return if (result.isSuccessful && result.body() != null) {
            Result.success(
                PostItems(
                    result.body()!!.post.map {
                        it.toDomain()
                    },
                    result.body()!!.isLast,
                ),
            )
        } else {
            Result.failure(CustomThrowable(result.code(), result.message()))
        }
    }
}
