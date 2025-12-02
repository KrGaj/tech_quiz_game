package com.example.techquiz.ui.screen

import com.example.techquiz.data.domain.UserAnswer
import com.example.techquiz.data.domain.AnswerOption
import com.example.techquiz.data.domain.Question
import kotlin.time.Duration

data class QuestionScreenState(
    val currentQuestion: Question = Question(),
    val questionNumber: Int = 0,
    val selectedAnswers: List<AnswerOption> = emptyList(),
    val userAnswers: List<UserAnswer> = emptyList(),
    val timeLeft: Duration? = null,
    val isLoading: Boolean = false,
    val isSendingAnswers: Boolean = false,
    val error: Throwable? = null,
)
