package com.example.techquiz.data.repository

import com.example.techquiz.data.domain.User

interface UserRepository {
    suspend fun getUser(
        token: String?,
    ): User

    fun closeHttpClient()
}
