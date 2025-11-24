package com.example.techquiz.data.repository

import com.example.techquiz.data.dto.response.stats.CategoryStats
import com.example.techquiz.data.dto.response.stats.CorrectAnswersStats
import com.example.techquiz.data.resources.Stats
import io.ktor.client.HttpClient
import io.ktor.client.call.body
import io.ktor.client.plugins.resources.get
import kotlin.uuid.ExperimentalUuidApi
import kotlin.uuid.Uuid

class StatsRepositoryDefault(
    private val httpClient: HttpClient,
) : StatsRepository {
    @OptIn(ExperimentalUuidApi::class)
    override suspend fun getMostAnsweredCategories(
        userUuid: Uuid?,
        count: Int,
    ): List<CategoryStats> {
        val response = httpClient.get(
            resource = Stats.MostAnsweredCategories(
                userUuid = userUuid,
                count = count,
            ),
        )

        return response.body()
    }

    @OptIn(ExperimentalUuidApi::class)
    override suspend fun getCorrectAnswersCount(
        userUuid: Uuid?,
    ): CorrectAnswersStats {
        val response = httpClient.get(
            resource = Stats.CorrectAnswersCount(
                userUuid = userUuid,
            ),
        )

        return response.body()
    }
}
