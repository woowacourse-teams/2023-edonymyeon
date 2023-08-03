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
    private var page = Page()
    private val _searchResult = MutableLiveData<List<PostItemUiModel>>()
    val searchResult: LiveData<List<PostItemUiModel>> get() = _searchResult
    fun getSearchResult(query: String) {
        viewModelScope.launch {
            searchRepository.getSearchResult(query, page.value)
                .onSuccess { result ->
                    _searchResult.value = searchResult.value.orEmpty() + result.map {
                        it.toUiModel()
                    }
                    page = page.increasePage()
                }
        }
    }

    fun clearResult() {
        page.initPage()
        _searchResult.value = emptyList()
    }
}
