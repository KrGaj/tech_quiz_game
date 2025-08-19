package com.example.techquiz.util

import android.app.Activity
import android.content.Context
import android.content.ContextWrapper
import androidx.compose.runtime.MutableState

// https://stackoverflow.com/a/74696154
internal fun Context.findActivity(): Activity {
    var context = this
    while (context is ContextWrapper) {
        if (context is Activity) return context
        context = context.baseContext
    }
    throw IllegalStateException("Activity not found")
}

fun MutableState<Boolean>.toggleValue() {
    value = !value
}
