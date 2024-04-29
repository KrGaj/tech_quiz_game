package com.example.techquiz.data.resources

import com.example.techquiz.util.UUIDSerializer
import io.ktor.resources.Resource
import kotlinx.serialization.Serializable
import java.util.UUID

@Resource("/stats")
class Stats {
    @Resource("most_answered_categories")
    class MostAnsweredCategories(
        val parent: Stats = Stats(),
        @Serializable(with = UUIDSerializer::class) val userUUID: UUID?,
        val count: Int,
    )

    @Resource("correct_answers_count")
    class CorrectAnswersCount(
        val parent: Stats = Stats(),
        @Serializable(with = UUIDSerializer::class) val userUUID: UUID?,
    )
}
