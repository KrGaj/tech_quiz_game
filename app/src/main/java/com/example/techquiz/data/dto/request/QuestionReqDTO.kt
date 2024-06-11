package com.example.techquiz.data.dto.request

import com.example.techquiz.data.domain.Category
import kotlinx.serialization.Serializable

@Serializable
data class QuestionReqDTO(
    val id: Long,
    val category: Category,
)
