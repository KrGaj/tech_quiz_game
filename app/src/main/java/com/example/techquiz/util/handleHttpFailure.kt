package com.example.techquiz.util

import androidx.compose.material3.SnackbarHostState

suspend fun handleHttpFailure(
    snackbarHostState: SnackbarHostState,
    throwable: Throwable,
) {
    // TODO improve
    snackbarHostState.showSnackbar(throwable.message ?: "Demo HTTP Error")
}
