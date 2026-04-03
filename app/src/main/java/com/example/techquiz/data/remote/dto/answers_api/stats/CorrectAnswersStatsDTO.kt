package com.example.techquiz.data.remote.dto.answers_api.stats

import kotlinx.serialization.Serializable

@Serializable
data class CorrectAnswersStatsDTO(
    val correctAnswers: Long,
    val allAnswers: Long,
)
