package com.example.techquiz.data.repository

import com.example.techquiz.data.dto.response.stats.CategoryStats
import com.example.techquiz.data.dto.response.stats.CorrectAnswersStats
import com.example.techquiz.data.resources.Stats
import io.ktor.client.HttpClient
import io.ktor.client.call.body
import io.ktor.client.plugins.resources.get
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import kotlin.uuid.ExperimentalUuidApi
import kotlin.uuid.Uuid

class StatsRepositoryDefault(
    private val httpClient: HttpClient,
) : StatsRepository {
    @OptIn(ExperimentalUuidApi::class)
    override suspend fun getMostAnsweredCategories(
        userUuid: Uuid?,
        count: Int,
    ): Result<List<CategoryStats>> = withContext(Dispatchers.IO) {
        Result.runCatching {
            val response = httpClient.get(
                resource = Stats.MostAnsweredCategories(
                    userUuid = userUuid,
                    count = count,
                ),
            )

            return@runCatching response.body()
        }
    }

    @OptIn(ExperimentalUuidApi::class)
    override suspend fun getCorrectAnswersCount(
        userUuid: Uuid?,
    ): Result<CorrectAnswersStats> = withContext(Dispatchers.IO) {
        Result.runCatching {
            val response = httpClient.get(
                resource = Stats.CorrectAnswersCount(
                    userUuid = userUuid,
                ),
            )

            return@runCatching response.body()
        }
    }
}
