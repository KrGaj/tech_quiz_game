package com.example.techquiz.data.repository

import com.example.techquiz.data.dto.response.stats.CategoryStats
import com.example.techquiz.data.dto.response.stats.CorrectAnswersStats
import java.util.UUID

interface StatsRepository {
    suspend fun getMostAnsweredCategories(
        userUUID: UUID?,
        count: Int,
    ): Result<List<CategoryStats>>

    suspend fun getCorrectAnswersCount(
        userUUID: UUID?,
    ): Result<CorrectAnswersStats>
}
