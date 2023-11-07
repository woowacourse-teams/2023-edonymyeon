package com.app.edonymyeon.data.common

import retrofit2.Call
import retrofit2.CallAdapter
import retrofit2.Retrofit
import java.lang.reflect.ParameterizedType
import java.lang.reflect.Type

class EdonymyeonCallAdapterFactory : CallAdapter.Factory() {
    override fun get(
        returnType: Type,
        annotations: Array<out Annotation>,
        retrofit: Retrofit,
    ): CallAdapter<*, *>? {
        if (getRawType(returnType) != Call::class.java) return null
        check(returnType is ParameterizedType) {
            ERROR_NOT_MATCH_GENERIC_TYPE
        }

        val wrapperType = getParameterUpperBound(0, returnType)
        if (getRawType(wrapperType) != ApiResponse::class.java) return null
        check(wrapperType is ParameterizedType) {
            ERROR_NOT_MATCH_GENERIC_TYPE
        }

        val bodyType = getParameterUpperBound(0, wrapperType)
        return EdonymyeonCallAdapter(bodyType)
    }

    companion object {
        private const val ERROR_NOT_MATCH_GENERIC_TYPE =
            "Call return type must be parameterized as Call<Foo> or Call<? extends Foo>"
    }
}
