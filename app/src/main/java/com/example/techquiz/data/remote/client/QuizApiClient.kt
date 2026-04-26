package com.example.techquiz.data.remote.client

import com.example.techquiz.data.remote.client.resources.Categories
import com.example.techquiz.data.remote.client.resources.Questions
import com.example.techquiz.data.remote.dto.quiz_api.CategoriesDTO
import com.example.techquiz.data.remote.dto.quiz_api.QuestionsDTO
import io.ktor.client.HttpClient
import io.ktor.client.call.body
import io.ktor.client.engine.HttpClientEngine
import io.ktor.client.engine.okhttp.OkHttp
import io.ktor.client.plugins.HttpTimeout
import io.ktor.client.plugins.contentnegotiation.ContentNegotiation
import io.ktor.client.plugins.defaultRequest
import io.ktor.client.plugins.resources.Resources
import io.ktor.client.plugins.resources.get
import io.ktor.http.ContentType
import io.ktor.http.URLProtocol
import io.ktor.http.contentType
import io.ktor.serialization.kotlinx.json.json
import kotlinx.serialization.json.Json
import org.koin.core.annotation.Factory
import kotlin.time.Duration
import kotlin.time.Duration.Companion.seconds

@Factory
class QuizApiClient(
    engine: HttpClientEngine = OkHttp.create(),
    requestTimeout: Duration = 10.seconds,
) {
    private val client = HttpClient(engine) {
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
                host = "opentdb.com"
            }
            contentType(ContentType.Application.Json)
        }

        expectSuccess = true
    }

    suspend fun getCategories(): CategoriesDTO =
        client.get(
            resource = Categories(),
        ).body()

    suspend fun getQuestions(
        category: Int,
        amount: Int,
    ): QuestionsDTO = client.get(
        resource = Questions(
            category = category,
            amount = amount,
        ),
    ).body()
}
