package com.example.techquiz.data.database.dao

import androidx.room.Dao
import androidx.room.Query
import com.example.techquiz.data.database.entity.CategoryEntity

@Dao
fun interface CategoryDao {
    @Query("SELECT * FROM categories")
    suspend fun getAllCategories(): List<CategoryEntity>
}