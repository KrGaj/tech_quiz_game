package com.example.techquiz.domain.repository

import com.example.techquiz.data.domain.Category

fun interface CategoryRepository {
    suspend fun getAllCategories(): List<Category>
}
