package com.example.techquiz.data

import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.Job
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import kotlin.time.Duration
import kotlin.time.Duration.Companion.seconds

class Timer(
    dispatcher: CoroutineDispatcher = Dispatchers.Default,
) {
    val coroutineScope = CoroutineScope(Job() + dispatcher)
    private var timer: Job? = null

    private val _timeLeft = MutableStateFlow(INITIAL_TIMEOUT.seconds)
    val timeLeft get() = _timeLeft.asStateFlow()

    fun start(
        timeout: Duration,
        onTimeout: suspend () -> Unit,
    ) {
        if (timer == null) {
            timer = coroutineScope.launch {
                for (time in (timeout.inWholeSeconds downTo 0)) {
                    _timeLeft.value = time.seconds
                    delay(1.seconds)
                }

                onTimeout()
            }
        }
    }

    fun clear() {
        timer?.cancel()
        timer = null
    }

    companion object {
        private const val INITIAL_TIMEOUT = 10L
    }
}
