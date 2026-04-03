package com.example.techquiz.data.remote.dto.quiz_api

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class CategoriesDTO(
    @SerialName("trivia_categories")
    val content: List<CategoryDTO>,
)

@Serializable
data class CategoryDTO(
    val id: Int,
    val name: String,
)
