package com.example.techquiz.domain.repository

import com.example.techquiz.domain.models.UserAnswer
import kotlin.uuid.ExperimentalUuidApi
import kotlin.uuid.Uuid

fun interface UserAnswerRepository {
    @OptIn(ExperimentalUuidApi::class)
    suspend fun insertAnswers(
        userUuid: Uuid,
        answers: List<UserAnswer>,
    ): Result<Unit>
}
