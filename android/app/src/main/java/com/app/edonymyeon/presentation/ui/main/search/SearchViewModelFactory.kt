package com.app.edonymyeon.presentation.ui.main.search

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.domain.edonymyeon.repository.SearchRepository

class SearchViewModelFactory(private val searchRepository: SearchRepository) :
    ViewModelProvider.Factory {
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        return SearchViewModel(searchRepository) as T
    }
}
