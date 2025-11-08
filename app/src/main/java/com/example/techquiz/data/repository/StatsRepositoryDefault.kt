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
    ): List<CategoryStats> {
        val response = httpClient.get(
            resource = Stats.MostAnsweredCategories(
                userUUID = userUUID,
                count = count,
            ),
        )

        return response.body()
    }

    override suspend fun getCorrectAnswersCount(
        userUUID: UUID?,
    ): CorrectAnswersStats {
        val response = httpClient.get(
            resource = Stats.CorrectAnswersCount(
                userUUID = userUUID,
            ),
        )

        return response.body()
    }

    override fun closeHttpClient() {
        httpClient.close()
    }
}
