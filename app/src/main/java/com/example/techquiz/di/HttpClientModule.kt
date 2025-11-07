package com.example.techquiz.di

import com.example.techquiz.data.repository.UserDataStoreRepository
import io.ktor.client.HttpClient
import io.ktor.client.engine.okhttp.OkHttp
import io.ktor.client.plugins.HttpSend
import io.ktor.client.plugins.HttpTimeout
import io.ktor.client.plugins.contentnegotiation.ContentNegotiation
import io.ktor.client.plugins.contentnegotiation.ContentNegotiationConfig
import io.ktor.client.plugins.defaultRequest
import io.ktor.client.plugins.plugin
import io.ktor.client.plugins.resources.Resources
import io.ktor.http.ContentType
import io.ktor.http.URLProtocol
import io.ktor.http.contentType
import io.ktor.http.path
import io.ktor.serialization.kotlinx.json.json
import kotlinx.coroutines.flow.firstOrNull
import kotlinx.serialization.json.Json
import okhttp3.CertificatePinner
import okhttp3.OkHttpClient
import org.koin.core.qualifier.named
import org.koin.dsl.module
import java.util.Properties
import kotlin.time.Duration.Companion.seconds

const val QUIZ_API_CLIENT = "QuizApi"
const val TECH_QUIZ_BACKEND_CLIENT = "TechQuizBackend"

val httpClientModule = module {
    val requestTimeout = 10.seconds.inWholeMilliseconds

    single(named(QUIZ_API_CLIENT)) {
        HttpClient(OkHttp) {
            val envProperties = get<Properties>()
            val quizApiKey = envProperties.getProperty("quizApiKey")

            install(Resources)
            install(ContentNegotiation, ::configureContentNegotiation)
            install(HttpTimeout) {
                requestTimeoutMillis = requestTimeout
            }

            defaultRequest {
                url {
                    protocol = URLProtocol.HTTPS
                    host = "quizapi.io"
                    path("/api/v1/")
                    parameters.append("apiKey", quizApiKey)
                }
                contentType(ContentType.Application.Json)
            }

            expectSuccess = true
        }
    }

    single(named(TECH_QUIZ_BACKEND_CLIENT)) {
        HttpClient(OkHttp) {
            val envProperties = get<Properties>()
            val hostAddress = envProperties.getProperty("serverHost")
            val hostPort = envProperties.getProperty("serverPort").toInt()
            val pins = arrayOf(
                envProperties.getProperty("certificateSha"),
            )

            install(Resources)
            install(ContentNegotiation, ::configureContentNegotiation)
            install(HttpTimeout) {
                requestTimeoutMillis = requestTimeout
            }

            defaultRequest {
                url {
                    protocol = URLProtocol.HTTPS
                    host = hostAddress
                    port = hostPort
                }
                contentType(ContentType.Application.Json)
            }

            engine {
                preconfigured = createOkHttpClient(
                    host = hostAddress,
                    pins = pins,
                )
            }

            expectSuccess = true
        }.also {
            it.plugin(HttpSend).intercept { request ->
                val userDataStoreRepository: UserDataStoreRepository = get()
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
    }
}

private fun configureContentNegotiation(
    configure: ContentNegotiationConfig,
) = configure.json(
    Json {
        prettyPrint = true
        ignoreUnknownKeys = true
    }
)

private fun createOkHttpClient(
    host: String,
    pins: Array<String>,
): OkHttpClient {
    val certificatePinner = CertificatePinner.Builder()
        .add(
            pattern = host,
            pins = pins,
        )
        .build()

    return OkHttpClient.Builder()
        .certificatePinner(certificatePinner)
        .build()
}

