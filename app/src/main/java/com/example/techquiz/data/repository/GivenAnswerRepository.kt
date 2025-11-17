package com.example.techquiz.data.repository

import com.example.techquiz.data.domain.GivenAnswer
import java.util.UUID

fun interface GivenAnswerRepository {
    suspend fun insertAnswers(
        userUUID: UUID?,
        answers: List<GivenAnswer>,
    )
}
