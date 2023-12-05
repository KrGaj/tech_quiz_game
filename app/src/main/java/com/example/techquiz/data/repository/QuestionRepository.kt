package com.example.techquiz.data.repository

import com.example.techquiz.data.domain.Question

fun interface QuestionRepository {
    suspend fun getRandomQuestions(
        quantity: Int,
        categoryId: Int,
    ): List<Question>
}
