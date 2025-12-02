package com.example.techquiz.data.repository

import com.example.techquiz.data.domain.UserAnswer
import kotlin.uuid.ExperimentalUuidApi
import kotlin.uuid.Uuid

fun interface UserAnswerRepository {
    @OptIn(ExperimentalUuidApi::class)
    suspend fun insertAnswers(
        userUuid: Uuid,
        answers: List<UserAnswer>,
    )
}
