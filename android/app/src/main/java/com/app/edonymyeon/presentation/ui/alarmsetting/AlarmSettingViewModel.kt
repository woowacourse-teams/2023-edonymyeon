package com.app.edonymyeon.presentation.ui.alarmsetting

import androidx.lifecycle.MutableLiveData
import com.app.edonymyeon.presentation.common.viewmodel.BaseViewModel
import com.domain.edonymyeon.repository.ProfileRepository

class AlarmSettingViewModel(private val alarmRepository: ProfileRepository) : BaseViewModel() {
    private val _isPushAlarmOn = MutableLiveData<Boolean>()
    val isPushAlarmOn: MutableLiveData<Boolean> get() = _isPushAlarmOn
    private val _isOneReactionAlarmOn = MutableLiveData<Boolean>()
    val isOneReactionAlarmOn: MutableLiveData<Boolean> get() = _isOneReactionAlarmOn
    private val _isTenReactionAlarmOn = MutableLiveData<Boolean>()
    val isTenReactionAlarmOn: MutableLiveData<Boolean> get() = _isTenReactionAlarmOn

    private val _isConsumptionConfirmAlarmOn = MutableLiveData<Boolean>()
    val isConfirmConsumptionAlarmOn: MutableLiveData<Boolean> get() = _isConsumptionConfirmAlarmOn

    fun onPushAlarmSwitchEvent(isChecked: Boolean) {
        _isPushAlarmOn.value = isChecked
    }

    fun onOneReactionAlarmSwitchEvent(isChecked: Boolean) {
        _isOneReactionAlarmOn.value = isChecked
    }

    fun onTenReactionAlarmSwitchEvent(isChecked: Boolean) {
        _isTenReactionAlarmOn.value = isChecked
    }

    fun onConsumptionConfirmAlarmSwitchClicked(isChecked: Boolean) {
        _isConsumptionConfirmAlarmOn.value = isChecked
    }
}
