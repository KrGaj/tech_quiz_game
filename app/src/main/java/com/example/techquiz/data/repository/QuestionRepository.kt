package com.example.techquiz.data.repository

import com.example.techquiz.data.domain.Category
import com.example.techquiz.data.domain.Question

interface QuestionRepository {
    suspend fun getRandomQuestions(
        quantity: Int,
        category: Category,
    ): List<Question>

    fun closeHttpClient()
}
