package com.app.edonymyeon.presentation.ui.login

import android.text.Editable
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.app.edonymyeon.data.common.CustomThrowable
import com.app.edonymyeon.presentation.uimodel.LoginUiModel
import com.domain.edonymyeon.repository.AuthRepository
import kotlinx.coroutines.launch

class LoginViewModel(private val repository: AuthRepository) :
    ViewModel() {

    private val _loginInfo = MutableLiveData<LoginUiModel>()
    val loginInfo: LiveData<LoginUiModel>
        get() = _loginInfo

    private val _isLoginEnabled = MutableLiveData<Boolean>()
    val isLoginEnabled: LiveData<Boolean>
        get() = _isLoginEnabled

    private val _isSuccess = MutableLiveData<Boolean>()
    val isSuccess: LiveData<Boolean>
        get() = _isSuccess

    private val _errorMessage = MutableLiveData<String>()
    val errorMessage: LiveData<String>
        get() = _errorMessage

    fun onLoginClick() {
        setLoginEnable()
        if (_isLoginEnabled.value == true) {
            login()
        }
        _loginInfo.value = LoginUiModel("", "")
    }

    private fun login() {
        viewModelScope.launch {
            repository.login(
                _loginInfo.value?.email ?: "",
                _loginInfo.value?.password ?: "",
            ).onSuccess {
                _isSuccess.value = true
            }.onFailure {
                _isSuccess.value = false
                _errorMessage.postValue((it as CustomThrowable).message)
            }
        }
    }

    fun setEmail(editEmail: Editable) {
        val loginUiModel = _loginInfo.value ?: LoginUiModel("", "")
        _loginInfo.value = loginUiModel.copy(email = editEmail.toString())
    }

    fun setPassword(editPassword: Editable) {
        val loginUiModel = _loginInfo.value ?: LoginUiModel("", "")
        _loginInfo.value = loginUiModel.copy(password = editPassword.toString())
    }

    private fun setLoginEnable() {
        _isLoginEnabled.value =
            (_loginInfo.value?.email.isNullOrEmpty() || _loginInfo.value?.password.isNullOrEmpty()).not()
    }
}
