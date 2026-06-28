package com.example.techquiz.data.repository

import com.example.techquiz.data.domain.Category
import com.example.techquiz.domain.models.CategoryStats
import com.example.techquiz.domain.models.CorrectAnswersStats
import com.example.techquiz.data.remote.client.BackendApiClient
import com.example.techquiz.domain.repository.StatsRepository
import kotlin.uuid.ExperimentalUuidApi
import kotlin.uuid.Uuid

class StatsRepositoryDefault(
    private val apiClient: BackendApiClient,
) : StatsRepository {
    @OptIn(ExperimentalUuidApi::class)
    override suspend fun getMostAnsweredCategories(
        userUuid: Uuid,
        count: Int,
    ) = apiClient.getMostAnsweredCategories(
        userUuid = userUuid,
        count = count,
    ).map {
        CategoryStats(
            category = Category(
                id = 0, // For display purposes
                name = it.category.name,
            ),
            answersGiven = it.answersGiven,
        )
    }


    @OptIn(ExperimentalUuidApi::class)
    override suspend fun getCorrectAnswersCount(
        userUuid: Uuid,
    ): CorrectAnswersStats {
        val response = apiClient.getCorrectAnswersCount(
            userUuid = userUuid,
        )

        return CorrectAnswersStats(
            correctAnswers = response.correctAnswers,
            allAnswers = response.allAnswers,
        )
    }
}
