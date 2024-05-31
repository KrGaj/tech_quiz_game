package com.example.techquiz.data.repository

import com.example.techquiz.data.dto.response.stats.CategoryStats
import com.example.techquiz.data.dto.response.stats.CorrectAnswersStats
import java.util.UUID

interface StatsRepository {
    suspend fun getMostAnsweredCategories(
        token: String?,
        userUUID: UUID?,
        count: Int,
    ): List<CategoryStats>

    suspend fun getCorrectAnswersCount(
        token: String?,
        userUUID: UUID?,
    ): CorrectAnswersStats
}
