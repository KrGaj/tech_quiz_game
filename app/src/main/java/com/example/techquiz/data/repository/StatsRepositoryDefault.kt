package com.example.techquiz.data.repository

import com.example.techquiz.data.dto.response.stats.CategoryStats
import com.example.techquiz.data.dto.response.stats.CorrectAnswersStats
import com.example.techquiz.data.resources.Stats
import io.ktor.client.HttpClient
import io.ktor.client.call.body
import io.ktor.client.plugins.resources.get

class StatsRepositoryDefault(
    private val httpClient: HttpClient,
) : StatsRepository {
    override suspend fun getMostAnsweredCategories(
        count: Int,
    ): List<CategoryStats> {
        val response = httpClient.get(Stats.MostAnsweredCategories())

        return response.body()
    }

    override suspend fun getCorrectAnswersCount(): CorrectAnswersStats {
        val response = httpClient.get(
            Stats.CorrectAnswersCount()
        )
        return response.body()
    }
}
