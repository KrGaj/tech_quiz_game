package com.example.techquiz.domain.models

import kotlinx.serialization.Serializable

@Serializable
data class CorrectAnswersStats(
    val correctAnswers: Long,
    val allAnswers: Long,
)
