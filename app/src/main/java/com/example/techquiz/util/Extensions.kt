package com.example.techquiz.util

import android.app.Activity
import android.content.Context
import android.content.ContextWrapper
import androidx.compose.runtime.MutableState
import io.ktor.client.HttpClient
import io.ktor.client.plugins.resources.get
import io.ktor.client.plugins.resources.post
import io.ktor.client.request.HttpRequestBuilder
import io.ktor.client.statement.HttpResponse

// https://stackoverflow.com/a/74696154
internal fun Context.findActivity(): Activity {
    var context = this
    while (context is ContextWrapper) {
        if (context is Activity) return context
        context = context.baseContext
    }
    throw IllegalStateException("Activity not found")
}

internal suspend inline fun <reified T : Any> HttpClient.getWithToken(
    resource: T,
    token: String?,
    builder: HttpRequestBuilder.() -> Unit = {},
): HttpResponse = get(resource) {
    setAuthorizationHeader(token)
    builder()
}

internal suspend inline fun <reified T : Any> HttpClient.postWithToken(
    resource: T,
    token: String?,
    builder: HttpRequestBuilder.() -> Unit = {},
): HttpResponse = post(resource) {
    setAuthorizationHeader(token)
    builder()
}

private fun HttpRequestBuilder.setAuthorizationHeader(
    token: String?,
) {
    headers.append("Authorization", "Bearer $token")
}

fun MutableState<Boolean>.toggleValue() {
    value = !value
}
