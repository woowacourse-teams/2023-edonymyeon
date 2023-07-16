package com.app.edonymyeon.presentation.ui.posteditor

import android.app.Application
import android.text.Editable
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData

class PostEditorViewModel(application: Application) : AndroidViewModel(application) {
    private val images = mutableListOf<String>()
    private val _galleryImages = MutableLiveData<List<String>>()
    private val _postTitle = MutableLiveData<String>()
    private val _postPrice = MutableLiveData<String>()
    private val _postContent = MutableLiveData<String>()
    val galleryImages: LiveData<List<String>>
        get() = _galleryImages

    val postTitle: LiveData<String>
        get() = _postTitle

    val postPrice: LiveData<String>
        get() = _postPrice

    val postContent: LiveData<String>
        get() = _postContent

    init {
        _galleryImages.value = images
    }

    fun addSelectedImages(image: String) {
        images.add(image)
        _galleryImages.value = images
    }

    fun deleteSelectedImages(image: String) {
        images.remove(image)
        _galleryImages.value = images
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
