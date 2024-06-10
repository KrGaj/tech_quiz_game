package com.example.techquiz.util

import androidx.annotation.StringRes
import com.example.techquiz.R
import io.ktor.client.network.sockets.ConnectTimeoutException
import io.ktor.client.network.sockets.SocketTimeoutException
import io.ktor.client.plugins.ClientRequestException
import io.ktor.client.plugins.HttpRequestTimeoutException
import io.ktor.client.plugins.ServerResponseException

@StringRes fun getHttpFailureMessage(
    exception: Exception?,
): Int = when (exception) {
    is ClientRequestException -> R.string.client_error_message
    is ServerResponseException -> R.string.server_error_message
    is HttpRequestTimeoutException -> R.string.request_timeout_message
    is ConnectTimeoutException -> R.string.connect_timeout_message
    is SocketTimeoutException -> R.string.socket_timeout_message
    else -> R.string.other_error_message
}
