package com.app.edonymyeon.presentation.ui.posteditor

import android.text.Editable
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.app.edonymyeon.data.common.CustomThrowable
import com.app.edonymyeon.data.dto.request.PostEditorResponse
import com.app.edonymyeon.presentation.uimodel.PostUiModel
import com.domain.edonymyeon.repository.PostRepository
import kotlinx.coroutines.launch

class PostEditorViewModel(private val repository: PostRepository) : ViewModel() {
    private val images = mutableListOf<String>()
    private val _galleryImages = MutableLiveData<List<String>>()
    val galleryImages: LiveData<List<String>>
        get() = _galleryImages

    private val _postTitle = MutableLiveData<String>()
    val postTitle: LiveData<String>
        get() = _postTitle

    private val _postPrice = MutableLiveData<String>()
    val postPrice: LiveData<String>
        get() = _postPrice

    private val _postContent = MutableLiveData<String>()
    val postContent: LiveData<String>
        get() = _postContent

    private val _postId = MutableLiveData<Long>()

    init {
        _galleryImages.value = images
    }

    fun initViewModelOnUpdate(post: PostUiModel) {
        _postTitle.value = post.title
        _postPrice.value = post.price.toString()
        _postContent.value = post.content
        _galleryImages.value = images
    }

    fun savePost() {
        viewModelScope.launch {
            repository.savePost(
                _postTitle.value.toString(),
                _postContent.value.toString(),
                _postPrice.value?.toInt() ?: 0,
                _galleryImages.value?.toList() ?: listOf(""),
            ).onSuccess {
                _postId.value = (it as PostEditorResponse).id
            }.onFailure {
                it as CustomThrowable
                when (it.code) {
                }
            }
        }
    }

    fun updatePost(id: Long) {
        viewModelScope.launch {
            repository.updatePost(
                id,
                _postTitle.value.toString(),
                _postContent.value.toString(),
                _postPrice.value?.toInt() ?: 0,
                _galleryImages.value?.toList() ?: listOf(""),
            ).onSuccess {
                _postId.value = (it as PostEditorResponse).id
            }.onFailure {
                it as CustomThrowable
                when (it.code) {
                }
            }
        }
    }

    fun addSelectedImages(image: String) {
        images.add(image)
        _galleryImages.value = images.toList()
    }

    fun deleteSelectedImages(image: String) {
        images.remove(image)
        _galleryImages.value = images.toList()
    }

    fun setTitle(editTitle: Editable) {
        _postTitle.value = editTitle.toString()
    }

    fun setPrice(editPrice: Editable) {
        _postPrice.value = editPrice.toString()
    }

    fun setContent(editContent: Editable) {
        _postContent.value = editContent.toString()
    }
}
