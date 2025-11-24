package com.example.techquiz.data.repository

import com.example.techquiz.data.domain.User
import com.example.techquiz.data.dto.response.UserDTO
import io.ktor.client.HttpClient
import io.ktor.client.call.body
import io.ktor.client.plugins.resources.get
import kotlin.uuid.ExperimentalUuidApi

class UserRepositoryDefault(
    private val httpClient: HttpClient,
) : UserRepository {
    @OptIn(ExperimentalUuidApi::class)
    override suspend fun getUser(): User {
        val response = httpClient.get(
            resource = com.example.techquiz.data.resources.User(),
        )
        val body = response.body<UserDTO>()

        return User(
            uuid = body.uuid,
        )
    }
}
