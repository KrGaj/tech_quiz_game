package com.example.techquiz.viewmodel

import androidx.lifecycle.ViewModel
import com.example.techquiz.data.domain.Category
import com.example.techquiz.data.repository.CategoryRepository
import com.example.techquiz.util.wrapAsResult
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import org.koin.core.component.KoinScopeComponent
import org.koin.core.component.createScope
import org.koin.core.component.inject
import org.koin.core.scope.Scope

class CategoryViewModel: ViewModel(), KoinScopeComponent {
    override val scope: Scope by lazy { createScope(this) }

    private val categoryRepository: CategoryRepository by inject()
    private val _categories = MutableStateFlow(Result.success(emptyList<Category>()))
    val categories get() = _categories.asStateFlow()

    suspend fun fetchCategories() = wrapAsResult {
        categoryRepository.getAllCategories()
    }.let { _categories.value = it }

    override fun onCleared() {
        super.onCleared()
        categoryRepository.closeHttpClient()
        scope.close()
    }

    companion object {
        const val COLUMNS_NUM = 2
    }
}
