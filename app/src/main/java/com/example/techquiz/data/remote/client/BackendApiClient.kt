package com.example.techquiz.data.remote.client

import com.example.techquiz.data.remote.client.resources.GivenAnswers
import com.example.techquiz.data.remote.dto.request.GivenAnswerDTO
import com.example.techquiz.data.repository.UserDataStoreRepository
import io.ktor.client.HttpClient
import io.ktor.client.engine.HttpClientEngine
import io.ktor.client.engine.okhttp.OkHttp
import io.ktor.client.plugins.HttpSend
import io.ktor.client.plugins.HttpTimeout
import io.ktor.client.plugins.contentnegotiation.ContentNegotiation
import io.ktor.client.plugins.defaultRequest
import io.ktor.client.plugins.plugin
import io.ktor.client.plugins.resources.Resources
import io.ktor.client.plugins.resources.post
import io.ktor.client.request.setBody
import io.ktor.http.ContentType
import io.ktor.http.URLProtocol
import io.ktor.http.contentType
import kotlinx.coroutines.flow.firstOrNull
import kotlinx.serialization.json.Json
import java.util.Properties
import kotlin.time.Duration
import kotlin.time.Duration.Companion.seconds

class BackendApiClient(
    engine: HttpClientEngine = OkHttp.create(),
    clientProperties: Properties,
    userDataStoreRepository: UserDataStoreRepository,
    requestTimeout: Duration = 10.seconds,
) {
    private val client = HttpClient(engine) {
        val hostAddress = clientProperties.getProperty("serverHost")
        val hostPort = clientProperties.getProperty("serverPort").toInt()

        install(Resources)
        install(ContentNegotiation) {
            Json {
                prettyPrint = true
                ignoreUnknownKeys = true
            }
        }
        install(HttpTimeout) {
            requestTimeoutMillis = requestTimeout.inWholeSeconds
        }

        defaultRequest {
            url {
                protocol = URLProtocol.HTTPS
                host = hostAddress
                port = hostPort
            }
            contentType(ContentType.Application.Json)
        }

        expectSuccess = true
    }.also {
        it.plugin(HttpSend).intercept { request ->
            val user = userDataStoreRepository.userFlow.firstOrNull()
            if (user != null) {
                request.headers.append(
                    name = "Authorization",
                    value = "Bearer ${user.userToken}",
                )
            }
            execute(request)
        }
    }

    suspend fun addUserAnswers(
        answers: List<GivenAnswerDTO>,
    ) {
        client.post(
            resource = GivenAnswers(),
        ) {
            setBody(answers)
        }
    }
}
