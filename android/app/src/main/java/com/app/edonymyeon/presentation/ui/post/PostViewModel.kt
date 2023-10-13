package com.app.edonymyeon.presentation.ui.post

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.viewModelScope
import com.app.edonymyeon.mapper.toUiModel
import com.app.edonymyeon.presentation.common.viewmodel.BaseViewModel
import com.app.edonymyeon.presentation.uimodel.PostItemUiModel
import com.domain.edonymyeon.model.Page
import com.domain.edonymyeon.repository.PostRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class PostViewModel @Inject constructor(
    private val postRepository: PostRepository,
) : BaseViewModel() {
    private var currentPage = Page()
    private var isLastPage = false

    private val _posts = MutableLiveData<List<PostItemUiModel>>()

    val posts: LiveData<List<PostItemUiModel>>
        get() = _posts

    val isLogin: Boolean
        get() = authRepository.getToken() != null
//        get() = PreferenceUtil.getValue(AuthLocalDataSource.USER_ACCESS_TOKEN) != null

    fun getPosts() {
        viewModelScope.launch(exceptionHandler) {
            postRepository.getPosts(20, currentPage.value).onSuccess { result ->
                _posts.value = posts.value.orEmpty() + result.posts.map {
                    it.toUiModel()
                }
                currentPage = currentPage.increasePage()
                isLastPage = result.isLast
            }
                .onFailure {
                    throw it
                }
        }
    }

    fun clearResult() {
        currentPage = currentPage.initPage()
        _posts.value = emptyList()
    }

    fun hasNextPage(): Boolean {
        return !isLastPage
    }
}
