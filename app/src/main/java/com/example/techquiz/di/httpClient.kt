package com.example.techquiz.di

import io.ktor.client.HttpClient
import io.ktor.client.HttpClientConfig
import io.ktor.client.engine.android.Android
import io.ktor.client.engine.android.AndroidEngineConfig
import io.ktor.client.plugins.contentnegotiation.ContentNegotiation
import io.ktor.client.plugins.defaultRequest
import io.ktor.client.plugins.resources.Resources
import io.ktor.http.ContentType
import io.ktor.http.URLBuilder
import io.ktor.http.contentType
import io.ktor.serialization.kotlinx.json.json
import kotlinx.serialization.json.Json
import org.koin.dsl.module

val httpClientModule = module {
    factory { (
                  urlBuilderBlock: UrlBuilderBlock,
                  additionalHttpClientConfig: AdditionalHttpClientConfig?,
        ) ->
        HttpClient(Android) {
            install(Resources)
            install(ContentNegotiation) {
                json(
                    Json {
                        prettyPrint = true
                        ignoreUnknownKeys = true
                    }
                )
            }

            additionalHttpClientConfig?.invoke(this)

            defaultRequest {
                url(urlBuilderBlock)
                contentType(ContentType.Application.Json)
            }

            expectSuccess = true
        }
    }
}

typealias UrlBuilderBlock = URLBuilder.() -> Unit

typealias AdditionalHttpClientConfig = (HttpClientConfig<AndroidEngineConfig>) -> Unit
