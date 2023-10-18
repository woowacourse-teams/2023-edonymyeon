package com.app.edonymyeon.data.repository

import com.app.edonymyeon.data.common.createCustomThrowableFromResponse
import com.app.edonymyeon.data.datasource.search.SearchDataSource
import com.app.edonymyeon.mapper.toDomain
import com.domain.edonymyeon.model.PostItems
import com.domain.edonymyeon.repository.SearchRepository
import javax.inject.Inject

class SearchRepositoryImpl @Inject constructor(
    private val searchDataSource: SearchDataSource,
) : SearchRepository {
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
            val customThrowable = createCustomThrowableFromResponse(result)
            Result.failure(customThrowable)
        }
    }
}
