package com.example.techquiz.data.repository

import com.example.techquiz.data.dto.response.stats.CategoryStats
import com.example.techquiz.data.dto.response.stats.CorrectAnswersStats
import com.example.techquiz.data.resources.Stats
import com.example.techquiz.util.getWithToken
import io.ktor.client.HttpClient
import io.ktor.client.call.body
import java.util.UUID

class StatsRepositoryDefault(
    private val httpClient: HttpClient,
) : StatsRepository {
    override suspend fun getMostAnsweredCategories(
        token: String?,
        userUUID: UUID?,
        count: Int,
    ): List<CategoryStats> {
        val response = httpClient.getWithToken(
            resource = Stats.MostAnsweredCategories(
                userUUID = userUUID,
                count = count,
            ),
            token = token,
        )

        return response.body()
    }

    override suspend fun getCorrectAnswersCount(
        token: String?,
        userUUID: UUID?,
    ): CorrectAnswersStats {
        val response = httpClient.getWithToken(
            resource = Stats.CorrectAnswersCount(
                userUUID = userUUID,
            ),
            token = token,
        )

        return response.body()
    }

    override fun closeHttpClient() {
        httpClient.close()
    }
}
