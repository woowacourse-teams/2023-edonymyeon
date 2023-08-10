package com.app.edonymyeon.presentation.ui.main.home

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.app.edonymyeon.presentation.uimodel.AllPostItemUiModel
import com.app.edonymyeon.presentation.uimodel.PostItemUiModel
import com.app.edonymyeon.presentation.uimodel.ReactionCountUiModel

class HomeViewModel : ViewModel() {
    private val _allPosts = MutableLiveData<List<AllPostItemUiModel>>()
    val allPosts: LiveData<List<AllPostItemUiModel>>
        get() = _allPosts

    private val _hotPosts = MutableLiveData<List<PostItemUiModel>>()
    val hotPosts: LiveData<List<PostItemUiModel>>
        get() = _hotPosts

    fun getAllPosts() {
        _allPosts.value = allPostDummy
        _hotPosts.value = hotPostDummy
    }

    private val allPostDummy = List(5) {
        AllPostItemUiModel(
            id = it.toLong(),
            title = "title $it",
            content = "content $it",
            nickname = "nickname $it",
            date = "date $it",
            thumbnailUrl = "thumbnailUrl $it",
        )
    }

    private val hotPostDummy = List(5) {
        PostItemUiModel(
            id = it.toLong(),
            title = "title $it",
            content = "content $it",
            thumbnailUrl = "thumbnailUrl $it",
            nickname = "nickname $it",
            createdAt = "date $it",
            reactionCount = ReactionCountUiModel(
                viewCount = it,
                commentCount = it,
                scrapCount = it,
            ),
        )
    }
}
