package com.app.edonymyeon.data.repository

import com.app.edonymyeon.data.datasource.search.SearchDataSource
import com.app.edonymyeon.mapper.toDomain
import com.app.edonymyeon.mapper.toResult
import com.domain.edonymyeon.model.PostItems
import com.domain.edonymyeon.repository.SearchRepository
import javax.inject.Inject

class SearchRepositoryImpl @Inject constructor(
    private val searchDataSource: SearchDataSource,
) : SearchRepository {
    override suspend fun getSearchResult(query: String, page: Int): Result<PostItems> {
        return searchDataSource.getSearchResult(query, page).toResult { it, _ ->
            PostItems(
                it.post.map { post ->
                    post.toDomain()
                },
                it.isLast,
            )
        }
    }
}
