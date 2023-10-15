package com.app.edonymyeon.data.util

interface TokenSharedPreference {
    fun getValue(key: String): String?
    fun setValue(key: String, value: String?)
}
