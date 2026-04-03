package com.example.techquiz.data.remote.dto.quiz_api

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class QuestionsDTO(
    @SerialName("results") val content: List<QuestionDTO>
)

@Serializable
data class QuestionDTO(
    @SerialName("question") val questionText: String,
    val category: String,
    @SerialName("correct_answer") val correctAnswer: String,
    @SerialName("incorrect_answers") val incorrectAnswers: List<String>,
)
