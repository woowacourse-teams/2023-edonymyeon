package com.app.edonymyeon.data.common

import com.bumptech.glide.load.HttpException
import okhttp3.Request
import okio.Timeout
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import java.net.SocketException
import java.net.UnknownHostException

class ResponseCall<T> constructor(
    private val callDelegate: Call<T>,
) : Call<Result<T>> {
    override fun enqueue(callback: Callback<Result<T>>) {
        callDelegate.enqueue(object : Callback<T> {
            override fun onResponse(call: Call<T>, response: Response<T>) {
                if (response.isSuccessful) {
                    response.body()?.let {
                        callback.onResponse(
                            this@ResponseCall,
                            Response.success(Result.Success(it, response.code())),
                        )
                    } ?: callback.onResponse(
                        this@ResponseCall,
                        Response.success(Result.NullResult()),
                    )
                } else {
                    when (response.code()) {
                        in 400..599 -> {
                            runCatching {
                                createCustomThrowableFromResponse(response)
                            }.onSuccess {
                                callback.onResponse(
                                    this@ResponseCall,
                                    Response.success(Result.ApiError(it)),
                                )
                            }.onFailure {
                                callback.onResponse(
                                    this@ResponseCall,
                                    Response.success(
                                        response.code(),
                                        Result.UnKnownApiError(response.code()),
                                    ),
                                )
                            }
                        }
                    }
                }
            }

            override fun onFailure(call: Call<T>, t: Throwable) {
                val fetchState = when (t) {
                    is SocketException -> FetchState.BadInternet
                    is HttpException -> FetchState.ParseError
                    is UnknownHostException -> FetchState.WrongConnection
                    else -> FetchState.Fail
                }
                callback.onResponse(
                    this@ResponseCall,
                    Response.success(Result.NetworkError(fetchState)),
                )
            }
        })
    }

    override fun clone(): Call<Result<T>> = ResponseCall(callDelegate.clone())

    override fun execute(): Response<Result<T>> =
        throw UnsupportedOperationException("ResponseCall does not support execute.")

    override fun isExecuted(): Boolean = callDelegate.isExecuted

    override fun cancel() = callDelegate.cancel()

    override fun isCanceled(): Boolean = callDelegate.isCanceled

    override fun request(): Request = callDelegate.request()

    override fun timeout(): Timeout = callDelegate.timeout()
}
