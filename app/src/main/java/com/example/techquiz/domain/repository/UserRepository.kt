package com.example.techquiz.domain.repository

import com.example.techquiz.domain.models.User

fun interface UserRepository {
    suspend fun getUser(): User
}
