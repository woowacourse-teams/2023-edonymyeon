package com.app.edonymyeon.presentation.ui.main.home

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.app.edonymyeon.data.common.CustomThrowable
import com.app.edonymyeon.mapper.toAllPostItemUiModel
import com.app.edonymyeon.presentation.uimodel.AllPostItemUiModel
import com.domain.edonymyeon.repository.PostRepository
import kotlinx.coroutines.launch

class HomeViewModel(private val repository: PostRepository) : ViewModel() {
    private val _allPosts = MutableLiveData<List<AllPostItemUiModel>>()
    val allPosts: LiveData<List<AllPostItemUiModel>>
        get() = _allPosts

    private val _allPostsSuccess = MutableLiveData<Boolean>()
    val allPostSuccess: LiveData<Boolean>
        get() = _allPostsSuccess

    fun getAllPosts() {
        viewModelScope.launch {
            repository.getPosts(ALL_POST_DEFAULT_SIZE, ALL_POST_DEFAULT_PAGE).onSuccess {
                _allPosts.value = it.posts.map { post ->
                    post.toAllPostItemUiModel()
                }
                _allPostsSuccess.value = true
            }.onFailure {
                it as CustomThrowable
                _allPostsSuccess.value = false
            }
        }
    }

    companion object {
        private const val ALL_POST_DEFAULT_SIZE = 5
        private const val ALL_POST_DEFAULT_PAGE = 0
    }
}
