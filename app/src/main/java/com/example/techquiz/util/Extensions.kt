package com.example.techquiz.util

import android.app.Activity
import android.content.Context
import android.content.ContextWrapper

// https://stackoverflow.com/a/74696154
internal fun Context.findActivity(): Activity {
    var context = this
    while (context is ContextWrapper) {
        if (context is Activity) return context
        context = context.baseContext
    }
    throw IllegalStateException("Activity not found")
}

internal inline fun <T> Collection<T>.allIfNotEmpty(
    predicate: (T) -> Boolean,
) = isEmpty() && all(predicate)
