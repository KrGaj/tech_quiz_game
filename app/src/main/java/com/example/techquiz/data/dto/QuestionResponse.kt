package com.example.techquiz.data.dto

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class QuestionResponse(
    val id: Int,
    @SerialName("question") val questionText: String,
    val description: String?,
    val answers: Map<String, String?>,
    @SerialName("correct_answers") val correctAnswers: Map<String, Boolean>,
    val category: String,
)
