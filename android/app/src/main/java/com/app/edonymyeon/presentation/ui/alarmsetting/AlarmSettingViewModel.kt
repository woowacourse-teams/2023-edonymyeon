package com.app.edonymyeon.presentation.ui.alarmsetting

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.viewModelScope
import com.app.edonymyeon.presentation.common.viewmodel.BaseViewModel
import com.domain.edonymyeon.model.NotificationPreference
import com.domain.edonymyeon.repository.AuthRepository
import com.domain.edonymyeon.repository.PreferenceRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class AlarmSettingViewModel @Inject constructor(
    private val preferenceRepository: PreferenceRepository,
    authRepository: AuthRepository,
) : BaseViewModel(authRepository) {
    private val _alarmPreference = MutableLiveData<List<NotificationPreference>>()
    val alarmPreference: LiveData<List<NotificationPreference>> get() = _alarmPreference

    private val _isPushAlarmOn = MutableLiveData<Boolean>()
    val isPushAlarmOn: LiveData<Boolean> get() = _isPushAlarmOn
    private val _isOneReactionAlarmOn = MutableLiveData<Boolean>()
    val isOneReactionAlarmOn: LiveData<Boolean> get() = _isOneReactionAlarmOn
    private val _isTenReactionAlarmOn = MutableLiveData<Boolean>()
    val isTenReactionAlarmOn: LiveData<Boolean> get() = _isTenReactionAlarmOn

    private val _isCommentAlarmOn = MutableLiveData<Boolean>()
    val isCommentAlarmOn: LiveData<Boolean> get() = _isCommentAlarmOn

    private val _isConsumptionConfirmAlarmOn = MutableLiveData<Boolean>()
    val isConfirmConsumptionAlarmOn: LiveData<Boolean> get() = _isConsumptionConfirmAlarmOn

    fun onPushAlarmSwitchEvent() {
        viewModelScope.launch(exceptionHandler) {
            preferenceRepository.saveNotificationPreference("0001").onSuccess { response ->
                response.map {
                    setNotificationSetting(it)
                }
            }
        }
    }

    fun onOneReactionAlarmSwitchEvent() {
        viewModelScope.launch(exceptionHandler) {
            preferenceRepository.saveNotificationPreference("1003").onSuccess { response ->
                response.map {
                    setNotificationSetting(it)
                }
            }
        }
    }

    fun onTenReactionAlarmSwitchEvent() {
        viewModelScope.launch(exceptionHandler) {
            preferenceRepository.saveNotificationPreference("1002").onSuccess { response ->
                response.map {
                    setNotificationSetting(it)
                }
            }
        }
    }

    fun onCommentAlarmSwitchEvent() {
        viewModelScope.launch(exceptionHandler) {
            preferenceRepository.saveNotificationPreference("2001").onSuccess { response ->
                response.map {
                    setNotificationSetting(it)
                }
            }
        }
    }

    fun onConsumptionConfirmAlarmSwitchClicked() {
        viewModelScope.launch(exceptionHandler) {
            preferenceRepository.saveNotificationPreference("5001").onSuccess { response ->
                response.map {
                    setNotificationSetting(it)
                }
            }
        }
    }

    fun getPushAlarmSetting() {
        viewModelScope.launch(exceptionHandler) {
            preferenceRepository.getNotificationPreference().onSuccess { response ->
                response.map {
                    setNotificationSetting(it)
                }
            }.onFailure {
                throw it
            }
        }
    }

    private fun setNotificationSetting(it: NotificationPreference) {
        when (it.preferenceType) {
            "0001" -> {
                _isPushAlarmOn.value = it.enabled
            }

            "1002" -> {
                _isTenReactionAlarmOn.value = it.enabled
            }

            "1003" -> {
                _isOneReactionAlarmOn.value = it.enabled
            }

            "2001" -> {
                _isCommentAlarmOn.value = it.enabled
            }

            "5001" -> {
                _isConsumptionConfirmAlarmOn.value = it.enabled
            }
        }
    }
}
