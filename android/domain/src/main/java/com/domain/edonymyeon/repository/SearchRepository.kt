package com.domain.edonymyeon.repository

import com.domain.edonymyeon.model.PostItem

interface SearchRepository {
    suspend fun getSearchResult(query: String, page: Int): Result<List<PostItem>>
}
