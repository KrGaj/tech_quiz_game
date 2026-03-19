package com.example.techquiz.app.ui.navigation

import androidx.compose.foundation.layout.consumeWindowInsets
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Scaffold
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.lifecycle.viewmodel.navigation3.rememberViewModelStoreNavEntryDecorator
import androidx.navigation3.runtime.NavKey
import androidx.navigation3.runtime.rememberSaveableStateHolderNavEntryDecorator
import androidx.navigation3.ui.NavDisplay
import org.koin.compose.navigation3.EntryProvider
import org.koin.core.annotation.KoinExperimentalAPI

@OptIn(KoinExperimentalAPI::class)
@Composable
fun NavigationRoot(
    navigator: Navigator,
    entryProvider: EntryProvider<NavKey>,
) {
    val navigationState by navigator.navigationState.collectAsStateWithLifecycle()

    val isNavBarVisible = remember(navigationState.currentBackStack.lastOrNull()) {
        navigationState.currentBackStack.lastOrNull()?.let {
            it is QuizRoute.Categories
                    || it is StatsRoute.Statistics
        } ?: false
    }

    Scaffold(
        bottomBar = {
            if (isNavBarVisible) {
                BottomNavBar(
                    currentTab = navigationState.currentTab,
                    onClick = navigator::onTabClick,
                )
            }
        }
    ) { innerPadding ->
        NavDisplay(
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding)
                .consumeWindowInsets(innerPadding),
            backStack = navigationState.currentBackStack,
            onBack = navigator::goBack,
            entryDecorators = listOf(
                rememberSaveableStateHolderNavEntryDecorator(),
                rememberViewModelStoreNavEntryDecorator(),
            ),
            entryProvider = entryProvider,
        )
    }
}
