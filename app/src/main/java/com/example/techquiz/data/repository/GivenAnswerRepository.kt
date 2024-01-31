package com.example.techquiz.data.repository

import com.example.techquiz.data.domain.GivenAnswer

fun interface GivenAnswerRepository {
    suspend fun insertAnswer(
        answer: GivenAnswer,
    )
}
