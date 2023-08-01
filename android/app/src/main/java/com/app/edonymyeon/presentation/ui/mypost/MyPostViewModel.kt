package com.app.edonymyeon.presentation.ui.mypost

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.app.edonymyeon.mapper.toUiModel
import com.app.edonymyeon.presentation.uimodel.MyPostUiModel
import com.domain.edonymyeon.model.Consumption
import com.domain.edonymyeon.model.MyPost
import com.domain.edonymyeon.repository.ProfileRepository

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
