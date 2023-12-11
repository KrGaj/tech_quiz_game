package com.example.techquiz.data.dto

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable
import java.util.SortedMap
import kotlin.reflect.full.memberProperties

@Serializable
data class QuestionResponse(
    val id: Int,
    @SerialName("question") val questionText: String,
    val description: String,
    val answers: SortedMap<String, String?>,
    @SerialName("multiple_correct_answers") val multipleCorrectAnswers: Boolean,
    @SerialName("correct_answers") val correctAnswers: SortedMap<String, Boolean>,
    val explanation: String,
    val tip: String?,
    val tags: List<String>,
    val category: String,
    val difficulty: String,
)
