package com.domain.edonymyeon.repository

interface ProfileRepository {
    suspend fun getProfile(): Result<Any>
}
