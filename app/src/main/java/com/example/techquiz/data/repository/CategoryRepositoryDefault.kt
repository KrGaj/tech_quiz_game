package com.example.techquiz.data.repository

import com.example.techquiz.data.database.dao.CategoryDao
import com.example.techquiz.data.domain.Category

class CategoryRepositoryDefault(
    private val categoryDao: CategoryDao,
) : CategoryRepository {
    override suspend fun getAllCategories(): List<Category> =
        categoryDao.getAllCategories().map {
            Category(
                it.id,
                it.name,
            )
        }
}
