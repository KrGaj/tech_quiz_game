package com.example.techquiz.data.repository

import com.example.techquiz.data.dto.response.stats.CategoryStats
import com.example.techquiz.data.dto.response.stats.CorrectAnswersStats
import com.example.techquiz.data.resources.Stats
import io.ktor.client.HttpClient
import io.ktor.client.call.body
import io.ktor.client.plugins.resources.get
import java.util.UUID

class StatsRepositoryDefault(
    private val httpClient: HttpClient,
) : StatsRepository {
    override suspend fun getMostAnsweredCategories(
        userUUID: UUID?,
        count: Int,
    ): Result<List<CategoryStats>> = Result.runCatching {
        val response = httpClient.get(
            resource = Stats.MostAnsweredCategories(
                userUUID = userUUID,
                count = count,
            ),
        )

        return@runCatching response.body()
    }

    override suspend fun getCorrectAnswersCount(
        userUUID: UUID?,
    ): Result<CorrectAnswersStats> = Result.runCatching {
        val response = httpClient.get(
            resource = Stats.CorrectAnswersCount(
                userUUID = userUUID,
            ),
        )

        return@runCatching response.body()
    }
}
