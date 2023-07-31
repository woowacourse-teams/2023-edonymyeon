package com.app.edonymyeon.data.datasource.auth

abstract class AuthDataSource {

    abstract fun getAuthToken(): String?
    abstract fun setAuthToken(token: String)
}
