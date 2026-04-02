package com.example.techquiz.data.remote.dto.response

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class QuestionDTO(
    @SerialName("question") val questionText: String,
    @SerialName("correct_answer") val correctAnswer: String,
    @SerialName("incorrect_answers") val incorrectAnswers: List<String>,
)
