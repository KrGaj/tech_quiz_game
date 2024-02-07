package com.example.techquiz.di

import android.content.Context
import io.ktor.client.HttpClient
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
import org.koin.android.ext.koin.androidContext
import org.koin.dsl.module

val httpClientModule = module {
    factory { (
                  urlBuilderBlock: UrlBuilderBlock,
                  engineConfigBlock: EngineConfigBlock?,
    ) ->
        HttpClient(Android) {
            install(Resources)
            install(ContentNegotiation) {
                json(
                    Json {
                        prettyPrint = true
                        isLenient = true
                        ignoreUnknownKeys = true
                    }
                )
            }

            engine {
                engineConfigBlock?.invoke(this, androidContext())
            }

            defaultRequest {
                url(urlBuilderBlock)
                contentType(ContentType.Application.Json)
            }

            expectSuccess = true
        }
    }
}

typealias UrlBuilderBlock = URLBuilder.() -> Unit

typealias EngineConfigBlock = (AndroidEngineConfig, Context) -> Unit
