package com.example.techquiz.app.di.navigation

import androidx.compose.animation.EnterTransition
import androidx.compose.animation.ExitTransition
import androidx.compose.animation.togetherWith
import androidx.navigation3.ui.NavDisplay
import com.example.techquiz.app.ui.navigation.Navigator
import com.example.techquiz.app.ui.navigation.QuizRoute
import com.example.techquiz.app.ui.navigation.StatsRoute
import com.example.techquiz.app.ui.question.QuestionScreen
import com.example.techquiz.app.ui.question.QuestionViewModel
import com.example.techquiz.domain.models.UserAnswer
import com.example.techquiz.ui.screen.CategoriesScreen
import com.example.techquiz.ui.screen.QuizSummaryScreen
import com.example.techquiz.ui.screen.StatsScreen
import org.koin.androidx.compose.koinViewModel
import org.koin.androidx.scope.dsl.activityRetainedScope
import org.koin.core.annotation.KoinExperimentalAPI
import org.koin.core.parameter.parametersOf
import org.koin.dsl.module
import org.koin.dsl.navigation3.navigation

@OptIn(KoinExperimentalAPI::class)
val navigationModule = module {
    activityRetainedScope {
        scoped {
            Navigator()
        }

        navigation<QuizRoute.Categories> {
            val navigator = get<Navigator>()

            CategoriesScreen(
                navigateToQuestionScreen = {
                    navigator.goTo(
                        destination = QuizRoute.Question(
                            category = it,
                        )
                    )
                }
            )
        }

        navigation<QuizRoute.Question> { navKey ->
            val navigator = get<Navigator>()

            val questionViewModel: QuestionViewModel = koinViewModel {
                parametersOf(navKey.category)
            }

            val navigateToSummary: (List<UserAnswer>) -> Unit = {
                navigator.goBack()
                navigator.goTo(
                    destination = QuizRoute.QuizSummary(
                        userAnswers = it,
                    )
                )
            }

            val navigateToCategories = {
                navigator.goBack()
            }

            QuestionScreen(
                questionViewModel = questionViewModel,
                navigateFromQuestion = { userAnswers ->
                    userAnswers.takeIf { it.isNotEmpty() }?.let {
                        navigateToSummary(it)
                    } ?: navigateToCategories()
                },
            )
        }

        navigation<QuizRoute.QuizSummary> {
            val navigator = get<Navigator>()

            QuizSummaryScreen(
                userAnswers = it.userAnswers,
                navigateToCategories = {
                    navigator.goBack()
                },
            )
        }

        navigation<StatsRoute.Statistics>(
            metadata = NavDisplay.popTransitionSpec {
                EnterTransition.None togetherWith ExitTransition.None
            } + NavDisplay.predictivePopTransitionSpec {
                EnterTransition.None togetherWith ExitTransition.None
            },
        ) {
            StatsScreen()
        }
    }
}
