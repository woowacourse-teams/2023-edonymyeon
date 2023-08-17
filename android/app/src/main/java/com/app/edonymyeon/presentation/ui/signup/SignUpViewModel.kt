package com.app.edonymyeon.presentation.ui.signup

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.domain.edonymyeon.model.Email
import com.domain.edonymyeon.model.Nickname
import com.domain.edonymyeon.model.Password
import com.domain.edonymyeon.model.UserRegistration
import com.domain.edonymyeon.repository.AuthRepository
import com.google.firebase.messaging.FirebaseMessaging
import kotlinx.coroutines.launch

class SignUpViewModel(private val authRepository: AuthRepository) : ViewModel() {
    private val _isEmailValid = MutableLiveData<Boolean>()
    val isEmailValid: LiveData<Boolean> get() = _isEmailValid

    private val _isNicknameValid = MutableLiveData<Boolean>()
    val isNicknameValid: LiveData<Boolean> get() = _isNicknameValid

    private val _isPasswordValid = MutableLiveData<Boolean>()
    val isPasswordValid: LiveData<Boolean> get() = _isPasswordValid

    private val _isPasswordCheckValid = MutableLiveData<Boolean>()
    val isPasswordCheckValid: LiveData<Boolean> get() = _isPasswordCheckValid

    private val _isSignUpAble = MutableLiveData<Boolean>()
    val isSignUpAble: LiveData<Boolean> get() = _isSignUpAble

    private val _isSignUpSuccess = MutableLiveData<Boolean>()
    val isSignUpSuccess: LiveData<Boolean> get() = _isSignUpSuccess

    fun signUp(email: String, password: String, nickname: String) {
        viewModelScope.launch {
            FirebaseMessaging.getInstance().token.result

            authRepository.signUp(
                UserRegistration(
                    Email.create(email),
                    Password.create(password),
                    Nickname.create(nickname),
                ),
            ).onSuccess {
                _isSignUpSuccess.value = true
            }.onFailure {
                _isSignUpSuccess.value = false
            }
        }
    }

    fun onEmailTextChanged(s: CharSequence, start: Int, before: Int, count: Int) {
        _isEmailValid.value = false
        checkSignUpAble()
    }

    fun onNicknameTextChanged(s: CharSequence, start: Int, before: Int, count: Int) {
        _isNicknameValid.value = false
        checkSignUpAble()
    }

    fun onPasswordTextChanged(s: CharSequence, start: Int, before: Int, count: Int) {
        _isPasswordCheckValid.value = false
        _isPasswordValid.value = Password.validate(s.toString())
        checkSignUpAble()
    }

    fun onPasswordCheckTextChanged(
        text: String,
        password: String,
    ) {
        _isPasswordCheckValid.value =
            Password.validate(text) && password == text
        checkSignUpAble()
    }

    private fun checkSignUpAble() {
        _isSignUpAble.value = isEmailValid.value ?: false &&
            isNicknameValid.value ?: false &&
            isPasswordValid.value ?: false &&
            isPasswordCheckValid.value ?: false
    }

    fun verifyEmail(email: String) {
        viewModelScope.launch {
            authRepository.checkDuplicate(EMAIL, email.trim()).onSuccess {
                _isEmailValid.value = it && Email.validate(email)
            }.onFailure {
                _isEmailValid.value = false
            }
        }
    }

    fun verifyNickname(nickname: String) {
        viewModelScope.launch {
            authRepository.checkDuplicate(NICKNAME, nickname.trim()).onSuccess {
                _isNicknameValid.value = it && Nickname.validate(nickname)
            }.onFailure {
                _isNicknameValid.value = false
            }
        }
    }

    companion object {
        private const val EMAIL = "email"
        private const val NICKNAME = "nickname"
    }
}
