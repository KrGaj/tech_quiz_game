package com.example.techquiz.data.remote.dto.answers_api

import kotlinx.serialization.Serializable

@Serializable
data class QuestionDTO(
    val text: String,
    val category: CategoryDTO,
)
