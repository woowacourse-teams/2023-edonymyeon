package com.app.edonymyeon.presentation.ui.posteditor

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.domain.edonymyeon.repository.PostRepository

class PostEditorViewModelFactory(private val repository: PostRepository) :
    ViewModelProvider.Factory {
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        return PostEditorViewModel(repository) as T
    }
}
