package com.example.techquiz.navigation

import android.net.Uri
import androidx.compose.material3.SnackbarHostState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.remember
import androidx.compose.ui.res.stringResource
import androidx.navigation.NavBackStackEntry
import androidx.navigation.NavController
import androidx.navigation.NavGraphBuilder
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.navArgument
import com.example.techquiz.R
import com.example.techquiz.data.domain.Category
import com.example.techquiz.data.domain.QuizResult
import com.example.techquiz.data.domain.QuizSummary
import com.example.techquiz.ui.screen.CategoriesScreen
import com.example.techquiz.ui.screen.LoginScreen
import com.example.techquiz.ui.screen.QuestionScreen
import com.example.techquiz.ui.screen.QuizSummaryScreen
import com.example.techquiz.ui.screen.StatsScreen
import com.example.techquiz.util.navtype.CategoryNavType
import com.example.techquiz.util.navtype.QuizSummaryNavType
import kotlinx.serialization.encodeToString
import kotlinx.serialization.json.Json

@Composable
fun AppNavHost(
    navController: NavHostController,
) {
    val snackbarHostState = remember { SnackbarHostState() }

    NavHost(
        navController = navController,
        startDestination = Screen.Login.route,
    ) {
        configureLoginScreenRoute(
            navGraphBuilder = this,
            navController,
        )

        configureCategoriesScreenRoute(
            navGraphBuilder = this,
            navController,
        )

        configureQuestionScreenRoute(
            navGraphBuilder = this,
            navController,
            snackbarHostState,
        )

        configureQuizResultsScreen(
            navGraphBuilder = this,
            navController,
        )

        configureStatsScreen(
            navGraphBuilder = this,
        )
    }
}

private fun configureLoginScreenRoute(
    navGraphBuilder: NavGraphBuilder,
    navController: NavController,
) {
    navGraphBuilder.composable(
        route = Screen.Login.route,
    ) {
        LoginScreen {
            navController.navigate(
                route = Screen.Categories.route,
            ) {
                popUpTo(
                    Screen.Login.route,
                ) {
                    inclusive = true
                }
            }
        }
    }
}

private fun configureCategoriesScreenRoute(
    navGraphBuilder: NavGraphBuilder,
    navController: NavController,
) {
    navGraphBuilder.composable(
        route = Screen.Categories.route,
    ) {
        CategoriesScreen {
            navController.navigate(
                route = "${Screen.Question.route}/${encodeCategory(it)}",
            ) {
                popUpTo(
                    Screen.Categories.route,
                ) {
                    inclusive = true
                }
            }
        }
    }
}

private fun configureQuestionScreenRoute(
    navGraphBuilder: NavGraphBuilder,
    navController: NavController,
    snackbarHostState: SnackbarHostState,
) {
    navGraphBuilder.composable(
        route = "${Screen.Question.route}/{${Screen.Question.navArg}}",
        arguments = listOf(navArgument(Screen.Question.navArg) {
            type = CategoryNavType
        }),
    ) { backStackEntry ->
        deserializeCategory(backStackEntry)?.let { category ->
            QuestionScreen(
                category = category,
                navigateToCategories = {
                    navController.navigate(Screen.Categories.route) {
                        popUpTo(Screen.Question.route) {
                            inclusive = true
                        }
                    }
                },
                navigateToResults = { navigateToResultsScreen(it, navController) },
            )
        } ?: run {
            val message = stringResource(id = R.string.navigation_question_error)

            LaunchedEffect(Unit) {
                snackbarHostState.showSnackbar(message)
            }
        }
    }
}

private fun configureQuizResultsScreen(
    navGraphBuilder: NavGraphBuilder,
    navController: NavController,
) {
    navGraphBuilder.composable(
        route = "${Screen.QuizSummary.route}/{${Screen.QuizSummary.navArg}}",
        arguments = listOf(navArgument(Screen.QuizSummary.navArg) {
            type = QuizSummaryNavType
        }),
    ) { backStackEntry ->
        val results = deserializeQuizResults(backStackEntry)

        QuizSummaryScreen(
            quizResults = results,
            onBackPressed = { backToCategoriesInclusive(navController) },
            navigateToCategories = { backToCategoriesInclusive(navController) },
        )
    }
}

private fun configureStatsScreen(
    navGraphBuilder: NavGraphBuilder,
) {
    navGraphBuilder.composable(
        route = Screen.Statistics.route,
    ) {
        StatsScreen()
    }
}

private fun navigateToResultsScreen(
    results: List<QuizResult>,
    navController: NavController,
) {
    val resultsJson = encodeQuizResults(results)
    navController.navigate(
        route = "${Screen.QuizSummary.route}/$resultsJson"
    ) {
        popUpTo(Screen.Question.route) {
            inclusive = true
        }
    }
}

private fun backToCategoriesInclusive(navController: NavController) {
    navController.navigate(Screen.Categories.route) {
        popUpTo(Screen.QuizSummary.route) {
            inclusive = true
        }
    }
}

private fun encodeCategory(
    category: Category,
): String = Uri.encode(Json.encodeToString(category))

private fun deserializeCategory(
    backStackEntry: NavBackStackEntry,
): Category? =
    backStackEntry.arguments?.let {
        if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.TIRAMISU) {
            it.getParcelable(Screen.Question.navArg, Category::class.java)
        } else {
            it.getParcelable(Screen.Question.navArg)
        }
    }

private fun encodeQuizResults(
    results: List<QuizResult>,
): String = Uri.encode(Json.encodeToString(QuizSummary(results)))

private fun deserializeQuizResults(
    backStackEntry: NavBackStackEntry,
): List<QuizResult> =
    backStackEntry.arguments?.let {
        if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.TIRAMISU) {
            it.getParcelable(Screen.QuizSummary.navArg, QuizSummary::class.java)
        } else {
            it.getParcelable(Screen.QuizSummary.navArg)
        }
    }?.results ?: emptyList()
