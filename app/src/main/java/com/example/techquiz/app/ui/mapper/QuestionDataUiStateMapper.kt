package com.example.techquiz.app.ui.mapper

import com.example.techquiz.app.ui.question.QuestionDataUiState
import com.example.techquiz.data.domain.Question

fun Question.toQuestionDataUiState(
    questionNumber: Int,
    isLast: Boolean,
) = QuestionDataUiState(
    questionText = text,
    options = options,
    questionNumber = questionNumber,
    isLast = isLast,
)
