package com.example.techquiz.viewmodel

import androidx.lifecycle.ViewModel
import com.example.techquiz.data.domain.Category
import com.example.techquiz.data.repository.CategoryRepository
import com.example.techquiz.util.wrapAsResult
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow

class CategoryViewModel(
    private val categoryRepository: CategoryRepository,
) : ViewModel() {
    private val _categories = MutableStateFlow(Result.success(emptyList<Category>()))
    val categories get() = _categories.asStateFlow()

    suspend fun fetchCategories() = wrapAsResult {
        categoryRepository.getAllCategories()
    }.let { _categories.value = it }

    companion object {
        const val COLUMNS_NUM = 2
    }
}
