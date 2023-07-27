package com.app.edonymyeon.presentation.ui.post

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.app.edonymyeon.mapper.toUiModel
import com.app.edonymyeon.presentation.uimodel.PostItemUiModel
import com.domain.edonymyeon.repository.PostRepository
import kotlinx.coroutines.launch

class PostViewModel(private val repository: PostRepository) : ViewModel() {

    private val _posts = MutableLiveData<List<PostItemUiModel>>()
    val posts: LiveData<List<PostItemUiModel>>
        get() = _posts

    fun getPosts(size: Int, page: Int) {
        viewModelScope.launch {
            repository.getPosts(size, page).onSuccess { posts ->
                _posts.value = posts.map {
                    it.toUiModel()
                }
            }
        }
    }
}
