package com.example.techquiz.data.remote.dto.answers_api.stats

import com.example.techquiz.data.remote.dto.answers_api.CategoryDTO
import kotlinx.serialization.Serializable

@Serializable
data class CategoryStatsDTO(
    val category: CategoryDTO,
    val answersGiven: Long,
)
