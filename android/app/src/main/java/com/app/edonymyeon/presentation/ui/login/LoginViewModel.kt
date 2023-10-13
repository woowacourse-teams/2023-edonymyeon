package com.app.edonymyeon.presentation.ui.login

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.viewModelScope
import com.app.edonymyeon.data.common.CustomThrowable
import com.app.edonymyeon.data.service.fcm.FCMToken
import com.app.edonymyeon.presentation.common.viewmodel.BaseViewModel
import com.domain.edonymyeon.repository.AuthRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class LoginViewModel @Inject constructor(
    private val repository: AuthRepository,
) : BaseViewModel() {

    private val _isSuccess = MutableLiveData<Boolean>()
    val isSuccess: LiveData<Boolean>
        get() = _isSuccess

    private val _errorMessage = MutableLiveData<String>()
    val errorMessage: LiveData<String>
        get() = _errorMessage

    fun onLoginClick(email: String?, password: String?) {
        if (email.isNullOrEmpty() || password.isNullOrEmpty()) {
            _errorMessage.value = LOGIN_ENABLE_ERROR_MESSAGE
        } else {
            login(email, password)
        }
    }

    private fun login(email: String, password: String) {
        FCMToken.getFCMToken {
            viewModelScope.launch(exceptionHandler) {
                repository.login(
                    email,
                    password,
                    it ?: "",
                ).onSuccess {
                    _isSuccess.value = true
                }.onFailure {
                    _isSuccess.value = false
                    throw it
                }
            }
        }
    }

    fun loginByKakao(accessToken: String) {
        FCMToken.getFCMToken {
            viewModelScope.launch(exceptionHandler) {
                repository.loginByKakao(
                    accessToken,
                    it.orEmpty(),
                ).onSuccess {
                    _isSuccess.value = true
                }.onFailure {
                    _isSuccess.value = false
                    _errorMessage.postValue((it as CustomThrowable).message)
                    throw it
                }
            }
        }
    }

    companion object {
        private const val LOGIN_ENABLE_ERROR_MESSAGE = "이메일과 패스워드는 필수 입력항목입니다."
    }
}
