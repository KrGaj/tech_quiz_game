package com.example.techquiz.data.repository

import com.example.techquiz.data.dto.response.stats.CategoryStats
import com.example.techquiz.data.dto.response.stats.CorrectAnswersStats

interface StatsRepository {
    suspend fun getMostAnsweredCategories(
        count: Int,
    ): List<CategoryStats>

    suspend fun getCorrectAnswersCount(): CorrectAnswersStats
}
