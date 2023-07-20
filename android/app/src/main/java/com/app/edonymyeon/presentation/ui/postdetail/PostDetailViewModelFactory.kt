package com.app.edonymyeon.presentation.ui.postdetail

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.domain.edonymyeon.repository.PostRepository

class PostDetailViewModelFactory(private val postId: Long, private val repository: PostRepository) :
    ViewModelProvider.Factory {
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        return PostDetailViewModel(postId, repository) as T
    }
}
