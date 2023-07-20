package com.app.edonymyeon.presentation.ui.posteditor

import android.text.Editable
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.app.edonymyeon.presentation.uimodel.PostUiModel

class PostEditorViewModel : ViewModel() {
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

    init {
        _galleryImages.value = images
    }

    fun init(post: PostUiModel) {
        _postTitle.value = post.title
        _postPrice.value = post.price.toString()
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
