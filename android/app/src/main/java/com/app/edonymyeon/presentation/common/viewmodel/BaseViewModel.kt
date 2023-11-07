package com.app.edonymyeon.presentation.common.viewmodel

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.app.edonymyeon.data.common.FetchState
import com.app.edonymyeon.data.service.client.calladapter.ApiException
import com.domain.edonymyeon.repository.AuthRepository
import kotlinx.coroutines.CoroutineExceptionHandler

open class BaseViewModel(val authRepository: AuthRepository) : ViewModel() {

    private val _fetchState = MutableLiveData<FetchState>()
    val fetchState: LiveData<FetchState>
        get() = _fetchState

    protected val exceptionHandler = CoroutineExceptionHandler { _, throwable ->
        throwable.printStackTrace()
        if (throwable is ApiException) {
            when (throwable) {
                is ApiException.NoAuthException ->
                    FetchState.NoAuthorization(throwable)

                is ApiException.HttpError -> _fetchState.value = FetchState.Fail(throwable)
                else -> FetchState.BadInternet
            }
        }
    }

    fun clearAuthToken() {
        authRepository.setToken(null)
    }

    companion object {
        private const val NO_AUTHORIZATION_CODE = 1523
    }
}
