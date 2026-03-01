package com.example.techquiz.app.ui.navigation

import app.cash.turbine.test
import com.example.techquiz.data.domain.Category
import io.kotest.assertions.throwables.shouldNotThrow
import io.kotest.matchers.shouldBe
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.test.StandardTestDispatcher
import kotlinx.coroutines.test.resetMain
import kotlinx.coroutines.test.runTest
import kotlinx.coroutines.test.setMain
import org.junit.Before
import org.junit.Test

class NavigatorTest {
    private lateinit var navigator: Navigator

    @OptIn(ExperimentalCoroutinesApi::class)
    @Before
    fun setUp() {
        val dispatcher = StandardTestDispatcher()
        Dispatchers.setMain(dispatcher)
        navigator = Navigator(dispatcher)
    }

    @OptIn(ExperimentalCoroutinesApi::class)
    fun tearDown() {
        Dispatchers.resetMain()
    }

    @Test
    fun `Navigator's default destination is home screen`() = runTest {
        navigator.navigationState.test {
            awaitItem().let {
                it.currentTab shouldBe NavTab.HOME
                it.currentBackStack shouldBe listOf(QuizRoute.Categories)
            }
        }
    }

    @Test
    fun `Selecting new tab updates backstack`() = runTest {
        navigator.navigationState.test {
            testScheduler.advanceUntilIdle()

            navigator.onTabClick(NavTab.STATISTICS)
            testScheduler.advanceUntilIdle()

            skipItems(1)
            awaitItem().let {
                it.currentTab shouldBe NavTab.STATISTICS
                it.currentBackStack shouldBe listOf(
                    QuizRoute.Categories,
                    StatsRoute.Statistics,
                )
            }
        }
    }

    @Test
    fun `Navigating to new destination updates backstack`() = runTest {
        val destination = QuizRoute.Question(
            Category(name = "Demo"),
        )

        navigator.navigationState.test {
            testScheduler.advanceUntilIdle()

            navigator.goTo(destination)
            testScheduler.advanceUntilIdle()

            skipItems(1)
            awaitItem().currentBackStack shouldBe listOf(
                QuizRoute.Categories,
                destination,
            )
        }
    }

    @Test
    fun `Going back navigates to previous screen in the same route`() = runTest {
        val questionDestination = QuizRoute.Question(
            Category(name = "Demo"),
        )
        val summaryDestination = QuizRoute.QuizSummary(emptyList())

        navigator.navigationState.test {
            testScheduler.advanceUntilIdle()

            navigator.goTo(questionDestination)
            testScheduler.advanceUntilIdle()

            navigator.goTo(summaryDestination)
            testScheduler.advanceUntilIdle()

            skipItems(2)
            awaitItem().currentBackStack shouldBe listOf(
                QuizRoute.Categories,
                questionDestination,
                summaryDestination,
            )

            navigator.goBack()
            testScheduler.advanceUntilIdle()

            awaitItem().currentBackStack shouldBe listOf(
                QuizRoute.Categories,
                questionDestination,
            )
        }
    }

    @Test
    fun `Going back navigates to the home route`() = runTest {
        val questionDestination = QuizRoute.Question(
            Category(name = "Demo"),
        )
        val summaryDestination = QuizRoute.QuizSummary(emptyList())

        navigator.navigationState.test {
            testScheduler.advanceUntilIdle()

            navigator.goTo(questionDestination)
            testScheduler.advanceUntilIdle()

            navigator.goTo(summaryDestination)
            testScheduler.advanceUntilIdle()

            navigator.onTabClick(NavTab.STATISTICS)
            testScheduler.advanceUntilIdle()

            skipItems(3)
            awaitItem().let {
                it.currentBackStack shouldBe listOf(
                    QuizRoute.Categories,
                    questionDestination,
                    summaryDestination,
                    StatsRoute.Statistics,
                )
                it.currentTab shouldBe NavTab.STATISTICS
            }

            navigator.goBack()
            testScheduler.advanceUntilIdle()

            awaitItem().let {
                it.currentBackStack shouldBe listOf(
                    QuizRoute.Categories,
                    questionDestination,
                    summaryDestination,
                )
                it.currentTab shouldBe NavTab.HOME
            }
        }
    }

    @Test
    fun `Navigator contains back stacks for all tabs`() = runTest {
        navigator.navigationState.test {
            testScheduler.advanceUntilIdle()

            shouldNotThrow<NoSuchElementException> {
                NavTab.entries.forEach { tab ->
                    navigator.onTabClick(tab)
                    testScheduler.advanceUntilIdle()
                }
            }

            cancelAndIgnoreRemainingEvents()
        }
    }
}
