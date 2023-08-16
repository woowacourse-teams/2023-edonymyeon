package com.app.edonymyeon.presentation.ui.posteditor

import android.app.Application
import android.content.Context
import android.net.Uri
import android.os.Build
import android.provider.MediaStore
import androidx.core.net.toUri
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.viewModelScope
import androidx.loader.content.CursorLoader
import com.app.edonymyeon.data.common.CustomThrowable
import com.app.edonymyeon.data.dto.response.PostEditorResponse
import com.app.edonymyeon.presentation.ui.mypost.dialog.ConsumptionDialog
import com.app.edonymyeon.presentation.uimodel.PostEditorUiModel
import com.app.edonymyeon.presentation.uimodel.PostUiModel
import com.domain.edonymyeon.repository.PostRepository
import kotlinx.coroutines.launch

class PostEditorViewModel(
    private val application: Application,
    private val repository: PostRepository,
) : AndroidViewModel(application) {
    private val images = mutableListOf<String>()
    private val _galleryImages = MutableLiveData<List<String>>()
    val galleryImages: LiveData<List<String>> get() = _galleryImages

    private val _isPostPriceValid = MutableLiveData<Boolean>()
    val isPostPriceValid: LiveData<Boolean> get() = _isPostPriceValid

    private val _isUploadAble = MutableLiveData<Boolean>()
    val isUpdateAble: LiveData<Boolean> get() = _isUploadAble

    private val _postEditor = MutableLiveData<PostEditorUiModel>()
    val postEditor: LiveData<PostEditorUiModel> get() = _postEditor

    private val _postId = MutableLiveData<Long>()
    val postId: LiveData<Long> get() = _postId

    fun initViewModelOnUpdate(post: PostUiModel) {
        _postEditor.value = PostEditorUiModel(post.title, post.price.toString(), post.content)
        images.addAll(post.images.map { it.toUri().toString() })
        _galleryImages.value = post.images.map { it.toUri().toString() }
    }

    fun savePost(title: String, content: String, price: Int) {
        viewModelScope.launch {
            repository.savePost(
                title,
                content,
                price,
                getImagesFilepath(
                    _galleryImages.value?.toList()?.map { Uri.parse(it) } ?: listOf(),
                ),
            ).onSuccess {
                _postId.value = (it as PostEditorResponse).id
            }.onFailure {
                it as CustomThrowable
            }
        }
    }

    fun updatePost(id: Long, title: String, content: String, price: Int) {
        viewModelScope.launch {
            repository.updatePost(
                id,
                title,
                content,
                price,
                getImagesFilepath(
                    _galleryImages.value?.toList()?.map { Uri.parse(it) } ?: listOf(),
                ),
            ).onSuccess {
                _postId.value = (it as PostEditorResponse).id
            }.onFailure {
                it as CustomThrowable
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

    fun checkPriceValidate(price: CharSequence, start: Int, end: Int, count: Int) {
        val postPrice = price.toString()
        runCatching {
            if (postPrice != ConsumptionDialog.BLANK) postPrice?.toInt() ?: 0
        }.onSuccess {
            _isPostPriceValid.value = true
        }.onFailure {
            _isPostPriceValid.value = false
        }
    }

    fun checkTitleValidate(title: String) {
        _isUploadAble.value = title.isNotBlank()
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
