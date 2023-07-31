package com.domain.edonymyeon.repository

interface AuthRepository {
    suspend fun login(email: String, password: String): Result<Any>
}
