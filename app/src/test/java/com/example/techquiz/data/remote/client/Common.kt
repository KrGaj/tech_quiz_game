package com.example.techquiz.data.remote.client

import io.ktor.client.engine.mock.MockEngine
import io.ktor.client.engine.mock.respond
import io.ktor.client.engine.mock.respondError
import io.ktor.http.HttpHeaders
import io.ktor.http.HttpStatusCode
import io.ktor.http.headersOf
import kotlinx.serialization.json.Json

inline fun <reified T> createMockEngine(
    testData: T,
) = MockEngine {
    val content = Json.encodeToString(testData)

    respond(
        content = content,
        headers = HEADERS,
    )
}

fun createMockEngineError(
    statusCode: HttpStatusCode,
) = MockEngine {
    respondError(
        status = statusCode,
        headers = HEADERS,
    )
}

val HEADERS = headersOf(HttpHeaders.ContentType, "application/json")
