package com.app.edonymyeon.presentation.ui.main.alarm

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.viewModelScope
import com.app.edonymyeon.data.datasource.auth.AuthLocalDataSource
import com.app.edonymyeon.mapper.toUiModel
import com.app.edonymyeon.presentation.common.viewmodel.BaseViewModel
import com.app.edonymyeon.presentation.uimodel.NotificationUiModel
import com.domain.edonymyeon.model.Page
import com.domain.edonymyeon.repository.NotificationRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class AlarmViewModel @Inject constructor(
    private val notificationRepository: NotificationRepository,
) : BaseViewModel() {
    private var currentPage = Page()
    private var isLastPage = false

    private val _isLogin: MutableLiveData<Boolean> = MutableLiveData()
    val isLogin: LiveData<Boolean> get() = _isLogin

    private val _notificationList: MutableLiveData<List<NotificationUiModel>> = MutableLiveData()
    val notificationList: LiveData<List<NotificationUiModel>> get() = _notificationList

    fun checkLogin() {
        _isLogin.value = AuthLocalDataSource().getAuthToken() != null
    }

    fun getAlarmList() {
        viewModelScope.launch(exceptionHandler) {
            val result = notificationRepository.getNotifications(PAGE_SIZE, currentPage.value)
            if (result.isSuccess) {
                _notificationList.value =
                    notificationList.value.orEmpty() +
                    result.getOrNull()?.notifications?.map {
                        it.toUiModel()
                    }.orEmpty()
                currentPage = currentPage.increasePage()
                isLastPage = result.getOrNull()?.isLast ?: true
            }
        }
    }

    fun clearResult() {
        currentPage = currentPage.initPage()
        _notificationList.value = emptyList()
    }

    fun hasNextPage(): Boolean {
        return !isLastPage
    }

    companion object {
        private const val PAGE_SIZE = 20
    }
}
