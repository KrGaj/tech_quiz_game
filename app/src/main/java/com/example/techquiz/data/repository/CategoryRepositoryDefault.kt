package com.example.techquiz.data.repository

import com.example.techquiz.data.domain.Category
import com.example.techquiz.data.dto.response.CategoryDTO
import com.example.techquiz.data.resources.Categories
import io.ktor.client.HttpClient
import io.ktor.client.call.body
import io.ktor.client.plugins.resources.get

class CategoryRepositoryDefault(
    private val httpClient: HttpClient,
) : CategoryRepository {
    override suspend fun getAllCategories(): List<Category> {
        val response = httpClient.get(Categories())
        val responseBody: List<CategoryDTO> = response.body()
        val categories = responseBody.map {
            Category(
                name = it.name,
            )
        }

        return categories
    }
}
