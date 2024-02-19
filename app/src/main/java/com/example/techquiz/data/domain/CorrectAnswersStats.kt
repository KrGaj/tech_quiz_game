package com.example.techquiz.data.domain

import kotlinx.serialization.Serializable

@Serializable
data class CorrectAnswersStats(
    val correctAnswers: Int,
    val allAnswers: Int,
)
