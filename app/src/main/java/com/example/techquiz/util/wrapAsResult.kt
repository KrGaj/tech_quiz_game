package com.example.techquiz.util

inline fun <reified T> wrapAsResult(
    block: () -> T,
): Result<T> =
    try {
        Result.success(block())
    } catch (e: Exception) {
        Result.failure(e)
    }
