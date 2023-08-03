package com.app.edonymyeon.presentation.ui.post

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.app.edonymyeon.mapper.toUiModel
import com.app.edonymyeon.presentation.uimodel.PostItemUiModel
import com.domain.edonymyeon.model.Page
import com.domain.edonymyeon.repository.PostRepository
import kotlinx.coroutines.launch

class PostViewModel(private val repository: PostRepository) : ViewModel() {
    private var currentPage = Page()
    private var isLastPage = false

    private val _posts = MutableLiveData<List<PostItemUiModel>>()
    val posts: LiveData<List<PostItemUiModel>>
        get() = _posts

    fun getPosts() {
        viewModelScope.launch {
            repository.getPosts(20, currentPage.value).onSuccess { result ->
                _posts.value = posts.value.orEmpty() + result.posts.map {
                    it.toUiModel()
                }
                currentPage = currentPage.increasePage()
                isLastPage = result.isLast
            }
        }
    }

    fun clearResult() {
        currentPage.initPage()
        _posts.value = emptyList()
    }

    fun hasNextPage(): Boolean {
        return !isLastPage
    }
}
