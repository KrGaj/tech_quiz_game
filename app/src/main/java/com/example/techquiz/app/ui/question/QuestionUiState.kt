package com.example.techquiz.app.ui.question

import androidx.compose.runtime.Immutable
import com.example.techquiz.data.domain.AnswerOption
import com.example.techquiz.data.domain.UserAnswer
import kotlin.time.Duration
import kotlin.time.Duration.Companion.seconds

@Immutable
data class QuestionUiState(
    val question: QuestionDataUiState? = null,
    val selectedAnswers: List<AnswerOption> = emptyList(),
    val userAnswers: List<UserAnswer> = emptyList(),
    val timeLeft: Duration = 30.seconds,
    val isLoading: Boolean = false,
    val isSendingAnswers: Boolean = false,
    val error: Throwable? = null,
)

@Immutable
data class QuestionDataUiState(
    val questionText: String,
    val options: List<AnswerOption>,
    val questionNumber: Int,
    val isLast: Boolean,
)
