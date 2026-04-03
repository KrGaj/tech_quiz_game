package com.example.techquiz.data.repository

import com.example.techquiz.domain.models.User
import com.example.techquiz.data.remote.client.BackendApiClient
import com.example.techquiz.domain.repository.UserRepository
import kotlin.uuid.ExperimentalUuidApi

class UserRepositoryDefault(
    private val apiClient: BackendApiClient,
) : UserRepository {
    @OptIn(ExperimentalUuidApi::class)
    override suspend fun getUser(): User {
        val response = apiClient.getUser()

        return User(
            uuid = response.uuid,
        )
    }
}
