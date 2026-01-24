package com.example.techquiz.app.ui.navigation

import androidx.compose.animation.EnterTransition
import androidx.compose.animation.ExitTransition
import androidx.compose.animation.togetherWith
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Scaffold
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.lifecycle.viewmodel.navigation3.rememberViewModelStoreNavEntryDecorator
import androidx.navigation3.runtime.NavEntry
import androidx.navigation3.runtime.NavKey
import androidx.navigation3.runtime.entryProvider
import androidx.navigation3.runtime.rememberDecoratedNavEntries
import androidx.navigation3.runtime.rememberNavBackStack
import androidx.navigation3.runtime.rememberSaveableStateHolderNavEntryDecorator
import androidx.navigation3.ui.NavDisplay
import com.example.techquiz.data.domain.Category
import com.example.techquiz.data.domain.QuizResult
import com.example.techquiz.ui.screen.CategoriesScreen
import com.example.techquiz.ui.screen.QuestionScreen
import com.example.techquiz.ui.screen.QuizSummaryScreen
import com.example.techquiz.ui.screen.StatsScreen

@Composable
fun NavigationRoot() {
    val quizBackStack = rememberNavBackStack(QuizRoute.Categories)
    val statsBackStack = rememberNavBackStack(StatsRoute.Statistics)

    var currentTab by rememberSaveable {
        mutableStateOf(NavTab.HOME)
    }

    val quizNavEntries = rememberDecoratedNavEntries(
        backStack = quizBackStack,
        entryDecorators = listOf(
            rememberSaveableStateHolderNavEntryDecorator(),
            rememberViewModelStoreNavEntryDecorator(),
        ),
        entryProvider = getQuizNavEntryProvider(
            navigateCategoriesToQuestion = {
                quizBackStack.add(
                    QuizRoute.Question(
                        category = it,
                    )
                )
            },
            navigateQuestionToCategories = {
                quizBackStack.removeLastOrNull()
            },
            navigateQuestionToResults = {
                quizBackStack.removeLastOrNull()
                quizBackStack.add(
                    QuizRoute.QuizSummary(
                        userAnswers = it,
                    )
                )
            },
            navigateResultsToCategories = {
                quizBackStack.clear()
                quizBackStack.add(
                    QuizRoute.Categories,
                )
            },
        )
    )

    val statsNavEntries = rememberDecoratedNavEntries(
        backStack = statsBackStack,
        entryDecorators = listOf(
            rememberSaveableStateHolderNavEntryDecorator(),
            rememberViewModelStoreNavEntryDecorator(),
        ),
        entryProvider = getStatsNavEntryProvider()
    )

    val navEntries = when (currentTab) {
        NavTab.HOME -> quizNavEntries
        NavTab.STATISTICS -> quizNavEntries + statsNavEntries
    }

    val isNavBarVisible = remember(navEntries.lastOrNull()) {
        navEntries.lastOrNull()?.let {
            it.contentKey == QuizRoute.Categories.toString()
                    || it.contentKey == StatsRoute.Statistics.toString()
        } ?: false
    }

    val onBack: () -> Unit = {
        when (currentTab) {
            NavTab.HOME -> {
                quizBackStack.removeLastOrNull()
            }

            NavTab.STATISTICS -> {
                if (statsBackStack.size > 1) {
                    statsBackStack.removeLastOrNull()
                } else {
                    currentTab = NavTab.HOME
                }
            }
        }
    }

    Scaffold(
        bottomBar = {
            if (isNavBarVisible) {
                BottomNavBar(
                    currentTab = currentTab,
                    onClick = {
                        currentTab = it
                    },
                )
            }
        }
    ) { innerPadding ->
        NavDisplay(
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding),
            entries = navEntries,
            onBack = onBack,
        )
    }
}

private fun getQuizNavEntryProvider(
    navigateCategoriesToQuestion: (Category) -> Unit,
    navigateQuestionToCategories: () -> Unit,
    navigateQuestionToResults: (List<QuizResult>) -> Unit,
    navigateResultsToCategories: () -> Unit,
): (NavKey) -> NavEntry<NavKey> = entryProvider {
    entry<QuizRoute.Categories> {
        CategoriesScreen(
            navigateToQuestionScreen = navigateCategoriesToQuestion,
        )
    }

    entry<QuizRoute.Question> { navKey ->
        QuestionScreen(
            category = navKey.category,
            navigateToCategories = navigateQuestionToCategories,
            navigateToResults = navigateQuestionToResults,
        )
    }

    entry<QuizRoute.QuizSummary> { navKey ->
        QuizSummaryScreen(
            quizResults = navKey.userAnswers,
            navigateToCategories = navigateResultsToCategories,
        )
    }
}

private fun getStatsNavEntryProvider(): (NavKey) -> NavEntry<NavKey> = entryProvider {
    entry<StatsRoute.Statistics>(
        metadata = NavDisplay.popTransitionSpec {
            EnterTransition.None togetherWith ExitTransition.None
        } + NavDisplay.predictivePopTransitionSpec {
            EnterTransition.None togetherWith ExitTransition.None
        },
    ) {
        StatsScreen()
    }
}
