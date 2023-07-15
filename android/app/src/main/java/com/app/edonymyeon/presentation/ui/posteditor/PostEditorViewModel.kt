package com.app.edonymyeon.presentation.ui.posteditor

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData

class PostEditorViewModel(application: Application) : AndroidViewModel(application) {
    private val images = mutableListOf<String>()
    private val _galleryImages = MutableLiveData<List<String>>()
    val galleryImages: LiveData<List<String>>
        get() = _galleryImages

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
}
