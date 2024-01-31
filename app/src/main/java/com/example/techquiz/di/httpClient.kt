package com.example.techquiz.di

import io.ktor.client.HttpClient
import io.ktor.client.plugins.contentnegotiation.ContentNegotiation
import io.ktor.client.plugins.defaultRequest
import io.ktor.client.plugins.resources.Resources
import io.ktor.http.URLBuilder
import io.ktor.serialization.kotlinx.json.json
import kotlinx.serialization.json.Json
import org.koin.dsl.module

val httpClientModule = module {
    single { (urlBuilderBlock: URLBuilder.() -> Unit) ->
        HttpClient {
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

            defaultRequest {
                url {
                    urlBuilderBlock()

//                    protocol = URLProtocol.HTTPS
//                    host = "quizapi.io"
//                    path("/api/v1/")
//                    parameters.append("apiKey", "bhMnTOPrriwHI7TJ22ZP3PQtUGqKhMvhCF1ItC9g")
                }
            }

            expectSuccess = true
        }
    }
}
