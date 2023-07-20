package com.app.edonymyeon.presentation.ui.main.home

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.app.edonymyeon.presentation.uimodel.AllPostItemUiModel

class HomeViewModel : ViewModel() {
    private val _allPosts = MutableLiveData<List<AllPostItemUiModel>>()
    val allPosts: LiveData<List<AllPostItemUiModel>>
        get() = _allPosts

    fun getAllPosts() {
        _allPosts.value = allPostDummy
    }

    private val allPostDummy = List(5) {
        AllPostItemUiModel(
            id = it.toLong(),
            title = "title $it",
            content = "content $it",
            nickname = "nickname $it",
            date = "date $it",
            thumbnailUrl = "thumbnailUrl $it"
        )
    }
}
