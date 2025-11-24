package com.example.techquiz.data.resources

import io.ktor.resources.Resource
import kotlin.uuid.ExperimentalUuidApi
import kotlin.uuid.Uuid

@Resource("/stats")
class Stats {
    @Resource("most_answered_categories")
    @OptIn(ExperimentalUuidApi::class)
    class MostAnsweredCategories(
        val parent: Stats = Stats(),
        val userUuid: Uuid?,
        val count: Int,
    )

    @Resource("correct_answers_count")
    @OptIn(ExperimentalUuidApi::class)
    class CorrectAnswersCount(
        val parent: Stats = Stats(),
        val userUuid: Uuid?,
    )
}
