package com.app.edonymyeon.presentation.ui.posteditor

import android.app.Application
import android.content.Context
import android.net.Uri
import android.os.Build
import android.provider.MediaStore
import android.text.Editable
import androidx.core.net.toUri
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.viewModelScope
import androidx.loader.content.CursorLoader
import com.app.edonymyeon.data.common.CustomThrowable
import com.app.edonymyeon.data.dto.response.PostEditorResponse
import com.app.edonymyeon.presentation.uimodel.PostUiModel
import com.domain.edonymyeon.repository.PostRepository
import kotlinx.coroutines.launch

class PostEditorViewModel(
    private val application: Application,
    private val repository: PostRepository,
) : AndroidViewModel(application) {
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
    val postId: LiveData<Long>
        get() = _postId

    init {
        _galleryImages.value = images
    }

    fun initViewModelOnUpdate(post: PostUiModel) {
        _postTitle.value = post.title
        _postPrice.value = post.price.toString()
        _postContent.value = post.content
        images.addAll(post.images.map { it.toUri().toString() })
        _galleryImages.value = post.images.map { it.toUri().toString() }
    }

    fun savePost() {
        viewModelScope.launch {
            repository.savePost(
                _postTitle.value.toString(),
                _postContent.value ?: "",
                _postPrice.value?.toInt() ?: 0,
                getImagesFilepath(
                    _galleryImages.value?.toList()?.map { Uri.parse(it) }
                        ?: listOf(),
                ),
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
                getImagesFilepath(
                    _galleryImages.value?.toList()?.map { Uri.parse(it) }
                        ?: listOf(),
                ),
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

    private fun getImagesFilepath(uris: List<Uri>): List<String> {
        return uris.map { uri ->
            getAbsolutePathFromUri(application.applicationContext, uri) ?: ""
        }
    }

    private fun getAbsolutePathFromUri(context: Context, uri: Uri): String? {
        return when (uri.scheme) {
            "content" -> getAbsolutePathFromContentUri(context, uri)
            "http" -> getAbsolutePathFromHttp(uri)
            else -> null
        }
    }

    private fun getAbsolutePathFromContentUri(context: Context, uri: Uri): String? {
        val projection = arrayOf(MediaStore.Images.Media.DATA)
        val cursor = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q) {
            context.contentResolver.query(uri, projection, null, null, null)
        } else {
            CursorLoader(context, uri, projection, null, null, null).loadInBackground()
        }
        cursor?.use {
            if (it.moveToFirst()) {
                val columnIndex = it.getColumnIndexOrThrow(MediaStore.Images.Media.DATA)
                return it.getString(columnIndex)
            }
        }
        return null
    }

    private fun getAbsolutePathFromHttp(uri: Uri): String = uri.toString()
}
