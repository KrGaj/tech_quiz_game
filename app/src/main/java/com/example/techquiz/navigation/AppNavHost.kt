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
import androidx.navigation.NavOptionsBuilder
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.navArgument
import com.example.techquiz.R
import com.example.techquiz.data.domain.Category
import com.example.techquiz.data.domain.UserAnswer
import com.example.techquiz.data.domain.QuizSummary
import com.example.techquiz.ui.screen.CategoriesScreen
import com.example.techquiz.ui.screen.LoginScreen
import com.example.techquiz.ui.screen.QuestionScreen
import com.example.techquiz.ui.screen.QuizSummaryScreen
import com.example.techquiz.ui.screen.StatsScreen
import com.example.techquiz.util.navtype.CategoryNavType
import com.example.techquiz.util.navtype.QuizSummaryNavType
import kotlinx.serialization.json.Json

@Composable
fun AppNavHost(
    navController: NavHostController,
) {
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
                popUpToInclusive(Screen.Login)
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
                popUpToInclusive(Screen.Categories)
            }
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

private fun configureQuestionScreenRoute(
    navGraphBuilder: NavGraphBuilder,
    navController: NavController,
) {
    navGraphBuilder.composable(
        route = "${Screen.Question.route}/{${Screen.Question.navArg}}",
        arguments = listOf(
            navArgument(Screen.Question.navArg) {
                type = CategoryNavType
            },
        ),
    ) { backStackEntry ->
        val snackbarHostState = remember {
            SnackbarHostState()
        }

        deserializeCategory(backStackEntry)?.let { category ->
            QuestionScreen(
                category = category,
                navigateToCategories = {
                    navController.navigate(Screen.Categories.route) {
                        popUpToInclusive(Screen.Question)
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

private fun navigateToResultsScreen(
    results: List<UserAnswer>,
    navController: NavController,
) {
    val resultsJson = encodeQuizResults(results)
    navController.navigate(
        route = "${Screen.QuizSummary.route}/$resultsJson"
    ) {
        popUpToInclusive(Screen.Question)
    }
}

private fun encodeQuizResults(
    results: List<UserAnswer>,
): String = Uri.encode(Json.encodeToString(QuizSummary(results)))

private fun configureQuizResultsScreen(
    navGraphBuilder: NavGraphBuilder,
    navController: NavController,
) {
    navGraphBuilder.composable(
        route = "${Screen.QuizSummary.route}/{${Screen.QuizSummary.navArg}}",
        arguments = listOf(
            navArgument(Screen.QuizSummary.navArg) {
                type = QuizSummaryNavType
            },
        ),
    ) { backStackEntry ->
        val results = deserializeQuizResults(backStackEntry)

        QuizSummaryScreen(
            userAnswers = results,
            onBackPressed = { backToCategoriesInclusive(navController) },
            navigateToCategories = { backToCategoriesInclusive(navController) },
        )
    }
}

private fun deserializeQuizResults(
    backStackEntry: NavBackStackEntry,
): List<UserAnswer> =
    backStackEntry.arguments?.let {
        if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.TIRAMISU) {
            it.getParcelable(Screen.QuizSummary.navArg, QuizSummary::class.java)
        } else {
            it.getParcelable(Screen.QuizSummary.navArg)
        }
    }?.results ?: emptyList()

private fun backToCategoriesInclusive(navController: NavController) {
    navController.navigate(Screen.Categories.route) {
        popUpToInclusive(Screen.QuizSummary)
    }
}

private fun NavOptionsBuilder.popUpToInclusive(
    screen: Screen,
) = popUpTo(screen.route) {
    inclusive = true
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
