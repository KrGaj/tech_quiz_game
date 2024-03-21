package com.example.techquiz.data.dto.response.stats

import com.example.techquiz.data.domain.Category
import kotlinx.serialization.Serializable

@Serializable
data class CategoryStats(
    val category: Category,
    val answersGiven: Long,
)
