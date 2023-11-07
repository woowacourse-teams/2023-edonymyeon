package com.app.edonymyeon.presentation.common.viewmodel

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.app.edonymyeon.data.common.CustomThrowable
import com.app.edonymyeon.data.common.FetchState
import com.bumptech.glide.load.HttpException
import com.domain.edonymyeon.repository.AuthRepository
import kotlinx.coroutines.CoroutineExceptionHandler
import java.net.SocketException
import java.net.UnknownHostException
import javax.inject.Inject

open class BaseViewModel @Inject constructor(val authRepository: AuthRepository) : ViewModel() {

    private val _fetchState = MutableLiveData<FetchState>()
    val fetchState: LiveData<FetchState>
        get() = _fetchState

    protected val exceptionHandler = CoroutineExceptionHandler { _, throwable ->
        throwable.printStackTrace()

//        when (throwable) {
//            is SocketException -> _fetchState.value = FetchState.BadInternet
//            is HttpException -> _fetchState.value = FetchState.ParseError
//            is UnknownHostException -> _fetchState.value = FetchState.WrongConnection
//            else -> {
//                if ((throwable as CustomThrowable).code == NO_AUTHORIZATION_CODE) {
//                    _fetchState.value = FetchState.NoAuthorization(throwable)
//                } else {
//                    _fetchState.value = FetchState.Fail(throwable)
//                }
//            }
//        }
    }

    fun clearAuthToken() {
        authRepository.setToken(null)
    }

    companion object {
        private const val NO_AUTHORIZATION_CODE = 1523
    }
}
