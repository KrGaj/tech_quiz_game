package com.example.techquiz.app.ui.mapper

import com.example.techquiz.app.ui.question.AnswerOptionUiState
import com.example.techquiz.app.ui.question.QuestionDataUiState
import com.example.techquiz.domain.models.AnswerOption
import com.example.techquiz.domain.models.Question

fun Question.toQuestionDataUiState(
    questionNumber: Int,
    selectedOptions: List<AnswerOption>,
    isLast: Boolean,
) = QuestionDataUiState(
    questionText = text,
    categoryName = category.name,
    options = options.map { it.toAnswerOptionUiState(selectedOptions) },
    multipleCorrectAnswers = options.count { it.isCorrect } > 1,
    questionNumber = questionNumber,
    isLast = isLast,
)

private fun AnswerOption.toAnswerOptionUiState(
    selectedOptions: List<AnswerOption>,
) = AnswerOptionUiState(
    option = this,
    isSelected = this in selectedOptions,
)
