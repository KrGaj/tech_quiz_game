package com.example.techquiz.domain.repository

import com.example.techquiz.data.domain.Category
import com.example.techquiz.domain.models.Question

fun interface QuestionRepository {
    suspend fun getRandomQuestions(
        category: Category,
        quantity: Int,
    ): Result<List<Question>>
}
