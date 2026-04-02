package com.example.techquiz.di

import com.example.techquiz.data.remote.client.BackendApiClient
import com.example.techquiz.data.remote.client.QuizApiClient
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
import org.koin.core.module.dsl.singleOf
import org.koin.core.qualifier.named
import org.koin.dsl.module
import java.util.Properties
import kotlin.time.Duration.Companion.seconds

const val QUIZ_API_CLIENT = "QuizApi"
const val TECH_QUIZ_BACKEND_CLIENT = "TechQuizBackend"

val httpClientModule = module {
    singleOf(::QuizApiClient)
    singleOf(::BackendApiClient)
}
