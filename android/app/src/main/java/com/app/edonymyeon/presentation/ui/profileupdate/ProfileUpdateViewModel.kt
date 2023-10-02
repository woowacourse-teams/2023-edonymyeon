package com.app.edonymyeon.presentation.ui.profileupdate

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import com.app.edonymyeon.presentation.common.viewmodel.BaseViewModel
import com.app.edonymyeon.presentation.uimodel.WriterUiModel

class ProfileUpdateViewModel : BaseViewModel() {
    private val _profile = MutableLiveData<WriterUiModel>()
    val profile: LiveData<WriterUiModel>
        get() = _profile

    fun initOriginalProfile(original: WriterUiModel) {
        _profile.value = WriterUiModel(original.id, original.nickname, original.profileImage)
    }
}
