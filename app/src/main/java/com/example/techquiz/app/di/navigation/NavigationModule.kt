package com.example.techquiz.app.di.navigation

import androidx.compose.animation.EnterTransition
import androidx.compose.animation.ExitTransition
import androidx.compose.animation.togetherWith
import androidx.navigation3.ui.NavDisplay
import com.example.techquiz.app.ui.navigation.Navigator
import com.example.techquiz.app.ui.navigation.QuizRoute
import com.example.techquiz.app.ui.navigation.StatsRoute
import com.example.techquiz.ui.screen.CategoriesScreen
import com.example.techquiz.ui.screen.QuestionScreen
import com.example.techquiz.ui.screen.QuizSummaryScreen
import com.example.techquiz.ui.screen.StatsScreen
import org.koin.androidx.scope.dsl.activityRetainedScope
import org.koin.core.annotation.KoinExperimentalAPI
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

        navigation<QuizRoute.Question> { question ->
            val navigator = get<Navigator>()

            QuestionScreen(
                category = question.category,
                navigateToCategories = {
                    navigator.goBack()
                },
                navigateToResults = {
                    navigator.goBack()
                    navigator.goTo(
                        destination = QuizRoute.QuizSummary(
                            userAnswers = it,
                        )
                    )
                },
            )
        }

        navigation<QuizRoute.QuizSummary> {
            val navigator = get<Navigator>()

            QuizSummaryScreen(
                quizResults = it.userAnswers,
                navigateToCategories = {
                    navigator.goBack()
                }
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
