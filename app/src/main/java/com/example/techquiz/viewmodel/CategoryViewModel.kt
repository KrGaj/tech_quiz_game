package com.example.techquiz.viewmodel

import androidx.lifecycle.ViewModel
import com.example.techquiz.data.domain.Category
import com.example.techquiz.data.repository.CategoryRepository
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow

class CategoryViewModel(
    private val categoryRepository: CategoryRepository,
): ViewModel() {
    private val _categories = MutableStateFlow<Result<List<Category>>?>(null)
    val categories get() = _categories.asStateFlow()

    suspend fun fetchCategories() = Result.runCatching {
        categoryRepository.getAllCategories()
    }.let { _categories.value = it }
}
