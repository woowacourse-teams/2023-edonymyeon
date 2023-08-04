package com.app.edonymyeon.presentation.ui.main.search

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.app.edonymyeon.mapper.toUiModel
import com.app.edonymyeon.presentation.uimodel.PostItemUiModel
import com.domain.edonymyeon.model.Page
import com.domain.edonymyeon.repository.SearchRepository
import kotlinx.coroutines.launch

class SearchViewModel(private val searchRepository: SearchRepository) : ViewModel() {
    private var currentPage = Page()
    private var isLastPage = false

    private val _searchResult = MutableLiveData<List<PostItemUiModel>>()
    val searchResult: LiveData<List<PostItemUiModel>> get() = _searchResult
    fun getSearchResult(query: String) {
        viewModelScope.launch {
            searchRepository.getSearchResult(query, currentPage.value)
                .onSuccess { result ->
                    _searchResult.value = searchResult.value.orEmpty() + result.posts.map {
                        it.toUiModel()
                    }
                    currentPage = currentPage.increasePage()
                    isLastPage = result.isLast
                }
        }
    }

    fun clearResult() {
        currentPage = currentPage.initPage()
        _searchResult.value = emptyList()
    }

    fun hasNextPage(): Boolean {
        return !isLastPage
    }
}
