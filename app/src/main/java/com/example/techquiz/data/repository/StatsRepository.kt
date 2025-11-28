package com.example.techquiz.data.repository

import com.example.techquiz.data.dto.response.stats.CategoryStats
import com.example.techquiz.data.dto.response.stats.CorrectAnswersStats
import kotlin.uuid.ExperimentalUuidApi
import kotlin.uuid.Uuid

interface StatsRepository {
    @OptIn(ExperimentalUuidApi::class)
    suspend fun getMostAnsweredCategories(
        userUuid: Uuid,
        count: Int,
    ): List<CategoryStats>

    @OptIn(ExperimentalUuidApi::class)
    suspend fun getCorrectAnswersCount(
        userUuid: Uuid,
    ): CorrectAnswersStats
}
