package com.domain.edonymyeon.repository

import com.domain.edonymyeon.model.PostItem
import com.domain.edonymyeon.model.PostItems

interface SearchRepository {
    suspend fun getSearchResult(query: String, page: Int): Result<PostItems>
}
