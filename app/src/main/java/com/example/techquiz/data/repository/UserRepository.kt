package com.example.techquiz.data.repository

import com.example.techquiz.data.domain.User

fun interface UserRepository {
    suspend fun getUser(): User
}
