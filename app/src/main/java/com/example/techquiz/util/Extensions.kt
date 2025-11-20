package com.example.techquiz.util

import androidx.compose.runtime.MutableState

fun MutableState<Boolean>.toggleValue() {
    value = !value
}
