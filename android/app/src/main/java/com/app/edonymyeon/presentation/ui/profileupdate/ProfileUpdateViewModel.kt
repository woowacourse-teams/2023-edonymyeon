package com.app.edonymyeon.presentation.ui.profileupdate

import android.net.Uri
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import com.app.edonymyeon.presentation.common.viewmodel.BaseViewModel
import com.app.edonymyeon.presentation.uimodel.WriterUiModel

class ProfileUpdateViewModel : BaseViewModel() {
    private val _profile = MutableLiveData<WriterUiModel>()
    val profile: LiveData<WriterUiModel>
        get() = _profile

    private val _newProfileImage = MutableLiveData<String?>()
    val newProfileImage: LiveData<String?>
        get() = _newProfileImage

    init {
        _newProfileImage.value = null
    }

    fun initOriginalProfile(original: WriterUiModel) {
        _profile.value = WriterUiModel(original.id, original.nickname, original.profileImage)
        _newProfileImage.value = original.profileImage
    }

    fun setNewProfileImage(image: String) {
        _newProfileImage.value = image
    }
}
