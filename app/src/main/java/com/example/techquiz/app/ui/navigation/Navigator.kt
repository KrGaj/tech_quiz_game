package com.example.techquiz.app.ui.navigation

import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.Job
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.flow.update
import kotlin.collections.plus

class Navigator(
    dispatcher: CoroutineDispatcher = Dispatchers.Default,
) {
    private val coroutineScope = CoroutineScope(context = dispatcher + Job())

    private val state = MutableStateFlow(
        State()
    )

    val navigationState = state.map {
        it.toNavigationState()
    }.stateIn(
        scope = coroutineScope,
        started = SharingStarted.WhileSubscribed(stopTimeoutMillis = 5000L),
        initialValue = State().toNavigationState(),
    )

    fun onTabClick(
        tab: NavTab,
    ) {
        state.update { it.copy(currentTab = tab) }
    }

    fun goTo(
        destination: Screen,
    ) {
        state.update {
            val updatedBackStacks = addToBackStack(
                backStacks = it.backStacks,
                currentTab = it.currentTab,
                destination = destination,
            )

            it.copy(backStacks = updatedBackStacks)
        }
    }

    private fun addToBackStack(
        backStacks: NavBackStacks,
        currentTab: NavTab,
        destination: Screen,
    ): NavBackStacks {
        val topBackStack = backStacks.getValue(currentTab)

        return backStacks.toMutableMap().also {
            it[currentTab] = topBackStack + destination
        }
    }

    fun goBack() {
        state.update {
            val topBackStack = it.backStacks.getValue(it.currentTab)

            if (it.currentTab == NavTab.HOME || topBackStack.size > 1) {
                it.copy(
                    backStacks = popBackStack(
                        backStacks = it.backStacks,
                        currentTab = it.currentTab,
                    ),
                )
            } else {
                it.copy(currentTab = NavTab.HOME)
            }
        }
    }

    private fun popBackStack(
        backStacks: NavBackStacks,
        currentTab: NavTab,
    ): NavBackStacks {
        val topBackStack = backStacks.getValue(currentTab)

        return backStacks.toMutableMap().also {
            it[currentTab] = topBackStack.dropLast(1)
        }
    }

    private data class State(
        val currentTab: NavTab = NavTab.HOME,
        val backStacks: NavBackStacks = mapOf(
            NavTab.HOME to listOf<QuizRoute>(QuizRoute.Categories),
            NavTab.STATISTICS to listOf<StatsRoute>(StatsRoute.Statistics),
        ),
    ) {
        fun toNavigationState(): NavigationState {
            val backStack = when(currentTab) {
                NavTab.HOME -> backStacks.getValue(currentTab)
                else -> backStacks.getValue(NavTab.HOME) +
                        backStacks.getValue(currentTab)
            }

            return NavigationState(
                currentTab = currentTab,
                currentBackStack = backStack,
            )
        }
    }

    data class NavigationState(
        val currentTab: NavTab,
        val currentBackStack: List<Screen>,
    )
}

private typealias NavBackStacks = Map<NavTab, List<Screen>>
