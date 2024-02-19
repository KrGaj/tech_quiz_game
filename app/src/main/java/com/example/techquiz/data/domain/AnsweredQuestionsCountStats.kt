package com.example.techquiz.data.domain

import kotlinx.serialization.Serializable

@Serializable
data class AnsweredQuestionsCountStats(
    val questionsAnswered: Int,
    val allQuestions: Int,
)
