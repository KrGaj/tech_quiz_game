package com.example.techquiz.data.repository

import com.example.techquiz.data.domain.Category

fun interface CategoryRepository {
    fun getAllCategories(): List<Category>
}
