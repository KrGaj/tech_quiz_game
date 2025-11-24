package com.example.techquiz.data.repository

import com.example.techquiz.data.domain.GivenAnswer
import kotlin.uuid.ExperimentalUuidApi
import kotlin.uuid.Uuid

fun interface GivenAnswerRepository {
    @OptIn(ExperimentalUuidApi::class)
    suspend fun insertAnswers(
        userUuid: Uuid?,
        answers: List<GivenAnswer>,
    )
}
