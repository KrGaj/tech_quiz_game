package com.example.techquiz.data.dto.request

import com.example.techquiz.data.domain.Category
import kotlinx.serialization.Serializable

@Serializable
data class QuestionDTO(
    val id: Long,
    val category: Category,
)
