package com.example.techquiz.ui.screen

import com.example.techquiz.data.domain.PossibleAnswer
import com.example.techquiz.data.domain.Question
import kotlin.time.Duration

data class QuestionScreenState(
    val currentQuestion: Question = Question(),
    val questionNumber: Int = 0,
    val selectedAnswers: List<PossibleAnswer> = emptyList(),
    val timeLeft: Duration? = null,
    val isLoading: Boolean = false,
    val isSendingAnswers: Boolean = false,
    val error: Throwable? = null,
)
