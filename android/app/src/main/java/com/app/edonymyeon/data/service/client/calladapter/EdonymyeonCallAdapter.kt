package com.app.edonymyeon.data.service.client.calladapter

import retrofit2.Call
import retrofit2.CallAdapter
import java.lang.reflect.Type

class EdonymyeonCallAdapter(private val responseType: Type) :
    CallAdapter<Any, EdonymyeonCall<Any>> {
    override fun responseType(): Type = responseType

    override fun adapt(call: Call<Any>): EdonymyeonCall<Any> {
        return EdonymyeonCall(call, responseType)
    }
}
