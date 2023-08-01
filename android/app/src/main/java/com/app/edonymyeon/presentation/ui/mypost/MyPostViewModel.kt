package com.app.edonymyeon.presentation.ui.mypost

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.app.edonymyeon.data.common.CustomThrowable
import com.app.edonymyeon.mapper.toUiModel
import com.app.edonymyeon.presentation.uimodel.MyPostUiModel
import com.domain.edonymyeon.model.Consumption
import com.domain.edonymyeon.model.MyPost
import com.domain.edonymyeon.repository.ProfileRepository
import kotlinx.coroutines.launch

class MyPostViewModel(val repository: ProfileRepository) : ViewModel() {

    private val _posts = MutableLiveData<List<MyPostUiModel>>()
    val posts: LiveData<List<MyPostUiModel>>
        get() = _posts

    fun getMyPosts(size: Int, page: Int) {
        /*viewModelScope.launch {
            repository.getMyPosts(size, page).onSuccess {
                it as MyPostsResponse
                _posts.value = it.posts.map { post ->
                    post.toUiModel()
                }
            }
        }*/
        _posts.value = fakeMyPost.map { it.toUiModel() }
    }

    fun postPurchaseConfirm(id: Long, purchasePrice: Int, year: Int, month: Int) {
        viewModelScope.launch {
            repository.postPurchaseConfirm(id, purchasePrice, year, month).onSuccess {
            }.onFailure {
                it as CustomThrowable
                when (it.code) {
                }
            }
        }
    }

    fun postSavingConfirm(id: Long, year: Int, month: Int) {
        viewModelScope.launch {
            repository.postSavingConfirm(id, year, month).onSuccess { }
                .onFailure {
                    it as CustomThrowable
                    when (it.code) {
                    }
                }
        }
    }

    fun deleteConfirm(id: Long) {
        viewModelScope.launch {
            repository.deleteConfirm(id).onSuccess {}
                .onFailure {
                    it as CustomThrowable
                    when (it.code) {
                    }
                }
        }
    }

    private val fakeMyPost = listOf(
        MyPost(
            id = 1L,
            title = "가나다라",
            image = "",
            content = "아야하언ㅇㄹㄴㅇㄹ",
            createdAt = "1일전",
            consumption = Consumption(
                type = "NONE",
                purchasePrice = 0,
                year = 0,
                month = 0,
            ),
        ),
        MyPost(
            id = 2L,
            title = "가나다라",
            image = "",
            content = "아야하언ㅇㄹㄴㅇㄹ",
            createdAt = "1일전",
            consumption = Consumption(
                type = "PURCHASE",
                purchasePrice = 1000,
                year = 2023,
                month = 7,
            ),
        ),
        MyPost(
            id = 3L,
            title = "가나다라",
            image = "",
            content = "아야하언ㅇㄹㄴㅇㄹ",
            createdAt = "1일전",
            consumption = Consumption(
                type = "SAVING",
                purchasePrice = 0,
                year = 2023,
                month = 4,
            ),
        ),
    )
}
