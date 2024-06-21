package com.example.techquiz.di

import io.ktor.client.HttpClient
import io.ktor.client.HttpClientConfig
import io.ktor.client.engine.android.Android
import io.ktor.client.engine.android.AndroidEngineConfig
import io.ktor.client.plugins.HttpTimeout
import io.ktor.client.plugins.contentnegotiation.ContentNegotiation
import io.ktor.client.plugins.defaultRequest
import io.ktor.client.plugins.resources.Resources
import io.ktor.http.ContentType
import io.ktor.http.URLBuilder
import io.ktor.http.contentType
import io.ktor.serialization.kotlinx.json.json
import kotlinx.serialization.json.Json
import org.koin.dsl.module
import kotlin.time.Duration.Companion.seconds

val httpClientModule = module {
    factory { (
                  urlBuilderBlock: UrlBuilderBlock,
                  sslManagerConfig: SslManagerConfig?,
        ) ->
        val requestTimeout = 10.seconds.inWholeMilliseconds

        HttpClient(Android) {
            install(Resources)
            install(ContentNegotiation, ::configureContentNegotiation)
            install(HttpTimeout) {
                requestTimeoutMillis = requestTimeout
            }

            sslManagerConfig?.invoke(this)

            configureDefaultRequest(this, urlBuilderBlock)

            expectSuccess = true
        }
    }
}

private fun configureContentNegotiation(
    configure: ContentNegotiation.Config,
) = configure.json(
    Json {
        prettyPrint = true
        ignoreUnknownKeys = true
    }
)

private fun configureDefaultRequest(
    configure: HttpClientConfig<AndroidEngineConfig>,
    urlBuilderBlock: UrlBuilderBlock,
) = configure.defaultRequest {
    url(urlBuilderBlock)
    contentType(ContentType.Application.Json)
}


typealias UrlBuilderBlock = URLBuilder.() -> Unit

typealias SslManagerConfig = (HttpClientConfig<AndroidEngineConfig>) -> Unit
