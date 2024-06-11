package com.example.techquiz.data.dto.response

import kotlinx.serialization.Serializable

@Serializable
data class CategoryDTO(
    val id: Int,
    val name: String,
)
