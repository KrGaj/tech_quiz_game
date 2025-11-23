package com.example.techquiz.data.repository

import com.example.techquiz.data.domain.Category
import com.example.techquiz.data.domain.Question

fun interface QuestionRepository {
    suspend fun getRandomQuestions(
        category: Category,
        quantity: Int,
    ): Result<List<Question>>
}
