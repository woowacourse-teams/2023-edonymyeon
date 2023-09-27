package com.app.edonymyeon.presentation.common.viewmodel

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.app.edonymyeon.data.common.FetchState
import com.bumptech.glide.load.HttpException
import kotlinx.coroutines.CoroutineExceptionHandler
import java.net.SocketException
import java.net.UnknownHostException

abstract class BaseViewModel() : ViewModel() {
    private val _fetchState = MutableLiveData<FetchState>()
    val fetchState: LiveData<FetchState>
        get() = _fetchState

    protected val exceptionHandler = CoroutineExceptionHandler { _, throwable ->
        throwable.printStackTrace()

        when (throwable) {
            is SocketException -> _fetchState.postValue(FetchState.BadInternet)
            is HttpException -> _fetchState.postValue(FetchState.ParseError)
            is UnknownHostException -> _fetchState.postValue(FetchState.WrongConnection)
            else -> _fetchState.postValue(FetchState.Fail)
        }
    }
}
