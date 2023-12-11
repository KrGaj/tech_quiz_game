package com.example.techquiz.di

import io.ktor.client.HttpClient
import io.ktor.client.plugins.contentnegotiation.ContentNegotiation
import io.ktor.client.plugins.defaultRequest
import io.ktor.client.plugins.resources.Resources
import io.ktor.serialization.kotlinx.json.json
import io.ktor.util.AttributeKey
import kotlinx.serialization.json.Json
import org.koin.dsl.module

val httpClientModule = module {
    single {
        HttpClient {
            install(Resources)
            install(ContentNegotiation) {
                json(
                    Json {
                        prettyPrint = true
                        isLenient = true
                    }
                )
            }

            defaultRequest {
                url("https://quizapi.io/api/v1/")   // TODO verify - we have 2 servers!
                setAttributes {
                    put(AttributeKey(name = "apiKey"), "")  // TODO
                }
            }

            expectSuccess = true
        }
    }
}
