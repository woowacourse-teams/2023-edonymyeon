package com.app.edonymyeon.data.datasource.auth

interface AuthDataSource {

    fun getAuthToken(): String?
    fun setAuthToken(token: String)
}
