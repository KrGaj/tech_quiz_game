package com.example.techquiz.app.ui.navigation

import androidx.navigation3.runtime.NavKey
import com.example.techquiz.data.domain.Category
import com.example.techquiz.data.domain.QuizResult
import kotlinx.serialization.Serializable

sealed interface Screen : NavKey

sealed class QuizRoute : Screen {
    @Serializable
    data object Categories : QuizRoute()

    @Serializable
    data class Question(
        val category: Category,
    ) : QuizRoute()

    @Serializable
    data class QuizSummary(
        val userAnswers: List<QuizResult>,
    ) : QuizRoute()
}

sealed class StatsRoute : Screen {
    @Serializable
    data object Statistics : StatsRoute()
}
