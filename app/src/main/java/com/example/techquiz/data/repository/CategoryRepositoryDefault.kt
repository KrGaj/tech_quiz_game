package com.example.techquiz.data.repository

import com.example.techquiz.data.domain.Category
import com.example.techquiz.data.remote.client.QuizApiClient

class CategoryRepositoryDefault(
    private val apiClient: QuizApiClient,
) : CategoryRepository {
    override suspend fun getAllCategories(): List<Category> {
        val responseBody = apiClient.getCategories()
        val categories = responseBody.map {
            Category(
                id = it.id,
                name = it.name,
            )
        }

        return categories
    }
}
