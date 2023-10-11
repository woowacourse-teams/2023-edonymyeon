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

    private val _newProfileImage = MutableLiveData<String?>()
    val newProfileImage: LiveData<String?>
        get() = _newProfileImage

    fun initOriginalProfile(original: WriterUiModel) {
        _profile.value = WriterUiModel(original.id, original.nickname, original.profileImage)
        _newProfileImage.value = original.profileImage
    }

    fun setNewProfileImage(image: String) {
        _newProfileImage.value = image
    }

    fun deleteProfileImage() {
        _newProfileImage.value = null
    }

    fun updateProfile(context: Context, nickname: String) {
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
                Nickname.create(nickname),
                image,
                isImageChanged,
            ).onFailure { it as CustomThrowable }
        }
    }
}
