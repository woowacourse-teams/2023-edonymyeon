package com.domain.edonymyeon.repository

interface UserRepository {
    suspend fun login(email: String, password: String): Result<Any>
}
