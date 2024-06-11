package com.example.techquiz.data.repository

import com.example.techquiz.data.domain.GivenAnswer
import java.util.UUID

interface GivenAnswerRepository {
    suspend fun insertAnswers(
        token: String?,
        userUUID: UUID?,
        answers: List<GivenAnswer>,
    )

    fun closeHttpClient()
}
