package com.example.techquiz.data.dto.response.stats

import kotlinx.serialization.Serializable

@Serializable
data class CorrectAnswersStats(
    val correctAnswers: Long = 0,
    val allAnswers: Long = 0,
)
