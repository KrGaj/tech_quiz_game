package com.example.techquiz.data

import io.kotest.matchers.shouldBe
import io.mockk.every
import io.mockk.mockk
import io.mockk.verify
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.test.StandardTestDispatcher
import kotlinx.coroutines.test.TestCoroutineScheduler
import kotlinx.coroutines.test.TestDispatcher
import kotlinx.coroutines.test.TestScope
import kotlinx.coroutines.test.advanceTimeBy
import kotlinx.coroutines.test.advanceUntilIdle
import org.junit.Before
import org.junit.Test
import kotlin.time.Duration.Companion.seconds
import kotlin.time.times

class TimerTest {
    private lateinit var testScheduler: TestCoroutineScheduler
    private lateinit var testDispatcher: TestDispatcher
    private lateinit var testScope: TestScope
    private lateinit var timer: Timer
    private val timeout = 5.seconds
    private val onTimeout: () -> Unit = mockk()

    @Before
    fun setUp() {
        testScheduler = TestCoroutineScheduler()
        testDispatcher = StandardTestDispatcher(testScheduler)
        testScope = TestScope(testDispatcher)
        timer = Timer(dispatcher = testDispatcher)
    }

    @OptIn(ExperimentalCoroutinesApi::class)
    @Test
    fun `Callback is invoked on timeout`() {
        every { onTimeout() } returns Unit

        timer.start(timeout, onTimeout)
        testScope.advanceUntilIdle()

        timer.timeLeft.value shouldBe 0.seconds
        verify(exactly = 1) { onTimeout() }
    }

    @OptIn(ExperimentalCoroutinesApi::class)
    @Test
    fun `Timer returns correct time left`() {
        val expectedTimeDiff = 2.seconds
        val delayTime = expectedTimeDiff + 0.1.seconds

        every { onTimeout() } returns Unit

        timer.start(timeout, onTimeout)

        testScope.advanceTimeBy(delayTime)
        val firstPoint = timer.timeLeft.value

        testScope.advanceTimeBy(delayTime)
        val secondPoint = timer.timeLeft.value

        testScope.advanceUntilIdle()

        firstPoint shouldBe timeout - expectedTimeDiff
        secondPoint shouldBe timeout - 2 * expectedTimeDiff
        timer.timeLeft.value shouldBe 0.seconds
    }

    @OptIn(ExperimentalCoroutinesApi::class)
    @Test
    fun `Cancelling timer stops counting time`() {
        val expectedTimeDiff = 2.seconds
        val delayTime = expectedTimeDiff + 0.1.seconds

        every { onTimeout() } returns Unit

        timer.start(timeout, onTimeout)
        testScope.advanceTimeBy(delayTime)
        timer.clear()

        timer.timeLeft.value shouldBe timeout - expectedTimeDiff

        testScope.advanceTimeBy(delayTime)
        timer.timeLeft.value shouldBe timeout - expectedTimeDiff
    }
}
