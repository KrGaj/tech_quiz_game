package com.example.techquiz.util

import androidx.annotation.StringRes
import com.example.techquiz.R
import io.ktor.client.plugins.ClientRequestException
import io.ktor.client.plugins.ResponseException
import io.ktor.client.plugins.ServerResponseException

@StringRes fun getHttpFailureMessage(
    exception: ResponseException?,
): Int = when (exception) {
    is ClientRequestException -> R.string.client_error_message
    is ServerResponseException -> R.string.server_error_message
    else -> R.string.other_error_message
}
