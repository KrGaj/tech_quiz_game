package com.example.techquiz.viewmodel

import androidx.lifecycle.ViewModel
import com.example.techquiz.data.domain.Category
import com.example.techquiz.data.repository.CategoryRepository
import com.example.techquiz.util.wrapAsResult
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.asSharedFlow
import org.koin.core.component.KoinScopeComponent
import org.koin.core.component.createScope
import org.koin.core.component.inject
import org.koin.core.scope.Scope

class CategoryViewModel: ViewModel(), KoinScopeComponent {
    override val scope: Scope by lazy { createScope(this) }

    private val categoryRepository: CategoryRepository by inject()

    private val _categories = MutableSharedFlow<Result<List<Category>>>()
    val categories get() = _categories.asSharedFlow()

    suspend fun fetchCategories() = wrapAsResult {
        categoryRepository.getAllCategories()
    }.let { _categories.emit(it) }

    override fun onCleared() {
        super.onCleared()
        categoryRepository.closeHttpClient()
        scope.close()
    }
}
