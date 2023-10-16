package com.app.edonymyeon.presentation.ui.posteditor

import android.content.Context
import android.net.Uri
import androidx.core.net.toUri
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.viewModelScope
import com.app.edonymyeon.data.dto.response.PostEditorResponse
import com.app.edonymyeon.presentation.common.imageutil.processAndAdjustImage
import com.app.edonymyeon.presentation.common.viewmodel.BaseViewModel
import com.app.edonymyeon.presentation.ui.mypost.dialog.ConsumptionDialog
import com.app.edonymyeon.presentation.uimodel.PostEditorUiModel
import com.app.edonymyeon.presentation.uimodel.PostUiModel
import com.domain.edonymyeon.model.PostEditor
import com.domain.edonymyeon.repository.AuthRepository
import com.domain.edonymyeon.repository.PostRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.launch
import java.io.File
import javax.inject.Inject

@HiltViewModel
class PostEditorViewModel @Inject constructor(
    private val repository: PostRepository,
    authRepository: AuthRepository,
) : BaseViewModel(authRepository) {
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
        viewModelScope.launch(exceptionHandler) {
            repository.savePost(
                postEditor,
                getFileFromContent(
                    context,
                    _galleryImages.value?.map { Uri.parse(it) } ?: emptyList(),
                ),
            ).onSuccess {
                _postId.value = (it as PostEditorResponse).id
            }.onFailure {
                throw it
            }
        }
    }

    fun updatePost(context: Context, id: Long, postEditor: PostEditor) {
        val uris = _galleryImages.value?.map { Uri.parse(it) } ?: emptyList()
        viewModelScope.launch(exceptionHandler) {
            repository.updatePost(
                id,
                postEditor,
                getAbsolutePathFromHttp(uris),
                getFileFromContent(context, uris),
            ).onSuccess {
                _postId.value = (it as PostEditorResponse).id
            }.onFailure {
                throw it
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
            if (postPrice != ConsumptionDialog.BLANK) {
                postPrice.toInt()
            }
        }.onSuccess {
            _isPostPriceValid.value = true
        }.onFailure {
            _isPostPriceValid.value = false
            throw it
        }
    }

    fun checkTitleValidate(title: String) {
        _isUploadAble.value = title.isNotBlank()
    }

    private fun getFileFromContent(context: Context, uris: List<Uri>): List<File> {
        return uris.filter { it.scheme == PREFIX_CONTENT }.map { uri ->
            processAndAdjustImage(context, uri)
        }
    }

    private fun getAbsolutePathFromHttp(uris: List<Uri>): List<String> {
        return uris.filter { it.scheme == PREFIX_HTTP }.map { uri ->
            uri.toString()
        }
    }

    companion object {
        private const val PREFIX_HTTP = "http"
        private const val PREFIX_CONTENT = "content"
    }
}
