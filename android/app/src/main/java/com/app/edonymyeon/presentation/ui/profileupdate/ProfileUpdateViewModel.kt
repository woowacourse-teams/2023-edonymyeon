package com.app.edonymyeon.presentation.ui.profileupdate

import android.content.Context
import android.net.Uri
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.viewModelScope
import com.app.edonymyeon.presentation.common.imageutil.processAndAdjustImage
import com.app.edonymyeon.presentation.common.viewmodel.BaseViewModel
import com.app.edonymyeon.presentation.uimodel.WriterUiModel
import com.domain.edonymyeon.model.Nickname
import com.domain.edonymyeon.repository.AuthRepository
import com.domain.edonymyeon.repository.ProfileRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.launch
import java.io.File
import javax.inject.Inject

@HiltViewModel
class ProfileUpdateViewModel @Inject constructor(
    private val repository: ProfileRepository,
    authRepository: AuthRepository,
) : BaseViewModel(authRepository) {
    private val _profile = MutableLiveData<WriterUiModel>()
    val profile: LiveData<WriterUiModel>
        get() = _profile

    private val _newNickname = MutableLiveData<String?>()

    private val _newProfileImage = MutableLiveData<String?>()
    val newProfileImage: LiveData<String?>
        get() = _newProfileImage

    private val _isNicknameValid = MutableLiveData<Boolean>(true)
    val isNicknameValid: LiveData<Boolean>
        get() = _isNicknameValid

    private val _isUploadSuccess = MutableLiveData<Boolean>()
    val isUploadSuccess: LiveData<Boolean>
        get() = _isUploadSuccess

    private val _isAbleToUpdate = MutableLiveData<Boolean>()
    val isAbleToUpdate: LiveData<Boolean>
        get() = _isAbleToUpdate

    private val isNicknameChanged: Boolean
        get() = _newNickname.value != _profile.value?.nickname.toString()

    private val isImageChanged: Boolean
        get() = _newProfileImage.value != _profile.value?.profileImage

    fun initOriginalProfile(original: WriterUiModel) {
        _profile.value = WriterUiModel(original.id, original.nickname, original.profileImage)
        _newProfileImage.value = original.profileImage
        _newNickname.value = original.nickname.toString()
    }

    fun setNewNickname(name: String) {
        _newNickname.value = name
        verifyNickname()
    }

    fun setNewProfileImage(image: String) {
        _newProfileImage.value = image
        checkAbleToUpdate()
    }

    fun deleteProfileImage() {
        _newProfileImage.value = null
        checkAbleToUpdate()
    }

    private fun verifyNickname() {
        if (!isNicknameChanged) {
            _isNicknameValid.value = true
            return
        }

        val newNickname = _newNickname.value ?: ""
        viewModelScope.launch(exceptionHandler) {
            repository.checkDuplicate(NICKNAME, newNickname)
                .onSuccess {
                    _isNicknameValid.value = it && Nickname.validate(newNickname)
                    checkAbleToUpdate()
                }.onFailure {
                    _isNicknameValid.value = false
                    throw it
                }
        }
    }

    private fun checkAbleToUpdate() {
        if (isNicknameValid.value == false) {
            _isAbleToUpdate.value = false
            return
        }
        if (isImageChanged || isNicknameChanged) {
            _isAbleToUpdate.value = true
            return
        }
        _isAbleToUpdate.value = false
    }

    fun updateProfile(context: Context) {
        val image: File? = if (_newProfileImage.value == null) {
            null
        } else if (isImageChanged) {
            processAndAdjustImage(context, Uri.parse(_newProfileImage.value))
        } else {
            null
        }

        viewModelScope.launch(exceptionHandler) {
            repository.updateProfile(
                Nickname.create(_newNickname.value!!),
                image,
                isImageChanged,
            ).onSuccess {
                _isUploadSuccess.value = true
            }.onFailure {
                throw it
            }
        }
    }

    companion object {
        private const val NICKNAME = "nickname"
    }
}
