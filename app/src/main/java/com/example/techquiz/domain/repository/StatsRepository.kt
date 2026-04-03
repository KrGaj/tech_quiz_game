package com.example.techquiz.domain.repository

import com.example.techquiz.domain.models.CategoryStats
import com.example.techquiz.domain.models.CorrectAnswersStats
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
