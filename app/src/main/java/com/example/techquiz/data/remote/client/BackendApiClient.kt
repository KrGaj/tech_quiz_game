package com.example.techquiz.data.remote.client

import com.example.techquiz.data.local.UserDataProvider
import com.example.techquiz.data.remote.client.resources.User
import com.example.techquiz.data.remote.client.resources.UserAnswers
import com.example.techquiz.data.remote.dto.answers_api.UserAnswerDTO
import com.example.techquiz.data.remote.dto.answers_api.UserDTO
import com.example.techquiz.data.remote.dto.answers_api.stats.CategoryStatsDTO
import com.example.techquiz.data.remote.dto.answers_api.stats.CorrectAnswersStatsDTO
import com.example.techquiz.data.resources.Stats
import io.ktor.client.HttpClient
import io.ktor.client.call.body
import io.ktor.client.engine.HttpClientEngine
import io.ktor.client.engine.okhttp.OkHttp
import io.ktor.client.plugins.HttpSend
import io.ktor.client.plugins.HttpTimeout
import io.ktor.client.plugins.contentnegotiation.ContentNegotiation
import io.ktor.client.plugins.defaultRequest
import io.ktor.client.plugins.plugin
import io.ktor.client.plugins.resources.Resources
import io.ktor.client.plugins.resources.get
import io.ktor.client.plugins.resources.post
import io.ktor.client.request.setBody
import io.ktor.http.ContentType
import io.ktor.http.URLProtocol
import io.ktor.http.contentType
import io.ktor.serialization.kotlinx.json.json
import kotlinx.coroutines.flow.firstOrNull
import kotlinx.serialization.json.Json
import org.koin.core.annotation.Factory
import java.util.Properties
import kotlin.time.Duration
import kotlin.time.Duration.Companion.seconds
import kotlin.uuid.ExperimentalUuidApi
import kotlin.uuid.Uuid

@Factory
class BackendApiClient(
    engine: HttpClientEngine = OkHttp.create(),
    clientProperties: Properties,
    userDataProvider: UserDataProvider,
    requestTimeout: Duration = 10.seconds,
) {
    private val client = HttpClient(engine) {
        val hostAddress = clientProperties.getProperty("serverHost")
        val hostPort = clientProperties.getProperty("serverPort").toInt()

        install(Resources)
        install(ContentNegotiation) {
            json(
                Json {
                    prettyPrint = true
                    ignoreUnknownKeys = true
                }
            )
        }
        install(HttpTimeout) {
            requestTimeoutMillis = requestTimeout.inWholeMilliseconds
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
            val user = userDataProvider.userFlow.firstOrNull()
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
        answers: List<UserAnswerDTO>,
    ) {
        client.post(
            resource = UserAnswers(),
        ) {
            setBody(answers)
        }
    }

    @OptIn(ExperimentalUuidApi::class)
    suspend fun getMostAnsweredCategories(
        userUuid: Uuid,
        count: Int,
    ): List<CategoryStatsDTO> = client.get(
        resource = Stats.MostAnsweredCategories(
            userUuid = userUuid,
            count = count,
        ),
    ).body()

    @OptIn(ExperimentalUuidApi::class)
    suspend fun getCorrectAnswersCount(
        userUuid: Uuid,
    ): CorrectAnswersStatsDTO = client.get(
        resource = Stats.CorrectAnswersCount(
            userUuid = userUuid,
        ),
    ).body()

    suspend fun getUser(): UserDTO = client.get(
        resource = User(),
    ).body()
}
