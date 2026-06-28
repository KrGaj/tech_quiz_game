package com.example.techquiz.data.repository

import com.example.techquiz.data.domain.Category
import com.example.techquiz.data.remote.client.QuizApiClient
import com.example.techquiz.domain.repository.CategoryRepository

class CategoryRepositoryDefault(
    private val apiClient: QuizApiClient,
) : CategoryRepository {
    override suspend fun getAllCategories(): List<Category> {
        val responseBody = apiClient.getCategories()
        val categories = responseBody.content.map {
            Category(
                id = it.id,
                name = it.name,
            )
        }

        return categories
    }
}
