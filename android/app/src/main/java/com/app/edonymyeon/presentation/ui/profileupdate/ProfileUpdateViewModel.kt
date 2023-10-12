package com.app.edonymyeon.presentation.ui.profileupdate

import android.content.Context
import android.net.Uri
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.viewModelScope
import com.app.edonymyeon.data.common.CustomThrowable
import com.app.edonymyeon.presentation.common.imageutil.processAndAdjustImage
import com.app.edonymyeon.presentation.common.viewmodel.BaseViewModel
import com.app.edonymyeon.presentation.uimodel.WriterUiModel
import com.domain.edonymyeon.model.Nickname
import com.domain.edonymyeon.repository.ProfileRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.launch
import java.io.File
import javax.inject.Inject

@HiltViewModel
class ProfileUpdateViewModel @Inject constructor(
    private val repository: ProfileRepository,
) : BaseViewModel() {
    private val _profile = MutableLiveData<WriterUiModel>()
    val profile: LiveData<WriterUiModel>
        get() = _profile

    private val _newNickname = MutableLiveData<String?>()

    private val _newProfileImage = MutableLiveData<String?>()
    val newProfileImage: LiveData<String?>
        get() = _newProfileImage

    private val _isUploadSuccess = MutableLiveData<Boolean>()
    val isUploadSuccess: LiveData<Boolean>
        get() = _isUploadSuccess

    private val _isAbleToUpdate = MutableLiveData<Boolean>()
    val isAbleToUpdate: LiveData<Boolean>
        get() = _isAbleToUpdate

    fun initOriginalProfile(original: WriterUiModel) {
        _profile.value = WriterUiModel(original.id, original.nickname, original.profileImage)
        _newProfileImage.value = original.profileImage
        _newNickname.value = original.nickname.toString()
    }

    fun setNewNickname(name: String) {
        _newNickname.value = name
        setAbleToUpdate()
    }

    fun setNewProfileImage(image: String) {
        _newProfileImage.value = image
        setAbleToUpdate()
    }

    fun deleteProfileImage() {
        _newProfileImage.value = null
        setAbleToUpdate()
    }

    private fun setAbleToUpdate() {
        if (_profile.value?.profileImage != _newProfileImage.value) {
            _isAbleToUpdate.value = true
            return
        }
        if (_profile.value?.nickname.toString() != _newNickname.value) {
            _isAbleToUpdate.value = true
            return
        }
        _isAbleToUpdate.value = false
    }

    fun updateProfile(context: Context) {
        val isImageChanged: Boolean = _newProfileImage.value != _profile.value?.profileImage
        val image: File? = if (_newProfileImage.value == null) {
            null
        } else if (isImageChanged) {
            processAndAdjustImage(context, Uri.parse(_newProfileImage.value))
        } else {
            null
        }

        /*
        * 이미지가 바뀜 -> 바뀐 이미지 전송
        * 이미지가 안바뀜 -> null 전송
        * */
        viewModelScope.launch(exceptionHandler) {
            repository.updateProfile(
                Nickname.create(_newNickname.value!!),
                image,
                isImageChanged,
            ).onSuccess {
                _isUploadSuccess.value = true
            }.onFailure {
                it as CustomThrowable
            }
        }
    }
}
