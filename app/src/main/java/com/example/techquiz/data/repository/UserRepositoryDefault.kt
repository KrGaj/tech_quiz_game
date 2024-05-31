package com.example.techquiz.data.repository

import com.example.techquiz.data.domain.User
import com.example.techquiz.data.dto.response.UserDTO
import com.example.techquiz.util.getWithToken
import io.ktor.client.HttpClient
import io.ktor.client.call.body

class UserRepositoryDefault(
    private val httpClient: HttpClient,
) : UserRepository {
    override suspend fun getUser(
        token: String?,
    ): User {
        val response = httpClient.getWithToken(
            resource = com.example.techquiz.data.resources.User(),
            token = token,
        )
        val body = response.body<UserDTO>()

        return User(
            uuid = body.uuid,
        )
    }
}
