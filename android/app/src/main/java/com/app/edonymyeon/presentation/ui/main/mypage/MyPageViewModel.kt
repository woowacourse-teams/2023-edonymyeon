package com.app.edonymyeon.presentation.ui.main.mypage

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.app.edonymyeon.presentation.uimodel.ConsumptionsUiModel

class MyPageViewModel : ViewModel() {
    private val _consumptions = MutableLiveData<ConsumptionsUiModel>()
    val consumptions: LiveData<ConsumptionsUiModel>
        get() = _consumptions
}
