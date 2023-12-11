package com.example.techquiz.util

import android.app.Activity
import android.content.Context
import android.content.ContextWrapper
import io.ktor.client.call.body
import io.ktor.client.statement.HttpResponse
import java.lang.Exception

// https://stackoverflow.com/a/74696154
internal fun Context.findActivity(): Activity {
    var context = this
    while (context is ContextWrapper) {
        if (context is Activity) return context
        context = context.baseContext
    }
    throw IllegalStateException("Activity not found")
}

internal suspend inline fun <reified T> HttpResponse.bodyAsResult() =
    try {
        val responseBody = body<T>()
        Result.success(responseBody)
    } catch (e: Exception) {
        Result.failure(e)
    }
