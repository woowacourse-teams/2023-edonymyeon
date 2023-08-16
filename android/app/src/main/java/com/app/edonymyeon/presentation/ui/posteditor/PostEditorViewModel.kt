package com.app.edonymyeon.presentation.ui.posteditor

import android.app.Application
import android.content.Context
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.media.ExifInterface
import android.net.Uri
import androidx.core.net.toUri
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.viewModelScope
import com.app.edonymyeon.data.common.CustomThrowable
import com.app.edonymyeon.data.dto.response.PostEditorResponse
import com.app.edonymyeon.presentation.ui.mypost.dialog.ConsumptionDialog
import com.app.edonymyeon.presentation.uimodel.PostEditorUiModel
import com.app.edonymyeon.presentation.uimodel.PostUiModel
import com.domain.edonymyeon.model.PostEditor
import com.domain.edonymyeon.repository.PostRepository
import kotlinx.coroutines.launch
import java.io.File
import java.io.FileOutputStream

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

    fun savePost(context: Context, postEditor: PostEditor) {
        viewModelScope.launch {
            repository.savePost(
                postEditor,
                getFileFromContent(
                    context,
                    _galleryImages.value?.map { Uri.parse(it) } ?: emptyList(),
                ),
            ).onSuccess {
                _postId.value = (it as PostEditorResponse).id
            }.onFailure {
                it as CustomThrowable
            }
        }
    }

    fun updatePost(context: Context, id: Long, postEditor: PostEditor) {
        val uris = _galleryImages.value?.map { Uri.parse(it) } ?: emptyList()
        viewModelScope.launch {
            repository.updatePost(
                id,
                postEditor,
                getAbsolutePathFromHttp(uris),
                getFileFromContent(context, uris),
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

    private fun getFileFromContent(context: Context, uris: List<Uri>): List<File> {
        return uris.filter { it.scheme == "content" }.map { uri ->
            uri.getBitmapFromUri(context)?.convertResizeImage(context) ?: File("")
        }
    }

    private fun getAbsolutePathFromHttp(uris: List<Uri>): List<String> {
        return uris.filter { it.scheme == "http" }.map { uri ->
            uri.toString()
        }
    }

    private fun Uri.getBitmapFromUri(context: Context): Bitmap? {
        return context.contentResolver
            .openInputStream(this)?.use {
            BitmapFactory.decodeStream(it)
        }
    }

    private fun Bitmap.convertResizeImage(context: Context): File {
        val tempFile = File.createTempFile("resized_image", ".jpg", context.cacheDir)
        val oldExifOrientation =
            ExifInterface(tempFile.absolutePath).getAttribute(ExifInterface.TAG_ORIENTATION)
        FileOutputStream(tempFile).use { fileOutputStream ->
            this.compress(Bitmap.CompressFormat.JPEG, 80, fileOutputStream)
        }

        tempFile.updateExifOrientation(oldExifOrientation ?: "")
        return tempFile
    }

    private fun File.updateExifOrientation(oldExifOrientation: String) {
        val newExifOrientation = ExifInterface(absolutePath)

        newExifOrientation.setAttribute(ExifInterface.TAG_ORIENTATION, oldExifOrientation)
        newExifOrientation.saveAttributes()
    }
}
