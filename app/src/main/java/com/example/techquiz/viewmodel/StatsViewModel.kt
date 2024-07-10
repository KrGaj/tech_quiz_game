package com.example.techquiz.viewmodel

import androidx.lifecycle.ViewModel
import com.example.techquiz.data.dto.response.stats.CategoryStats
import com.example.techquiz.data.dto.response.stats.CorrectAnswersStats
import com.example.techquiz.data.repository.StatsRepository
import com.example.techquiz.util.wrapAsResult
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import org.koin.core.component.KoinScopeComponent
import org.koin.core.component.createScope
import org.koin.core.component.inject
import org.koin.core.scope.Scope
import java.util.UUID

class StatsViewModel : ViewModel(), KoinScopeComponent {
    override val scope: Scope by lazy { createScope(this) }

    private val statsRepository: StatsRepository by inject()

    private val _categoryStats = MutableStateFlow<Result<List<CategoryStats>>?>(null)
    val categoryStats get() = _categoryStats.asStateFlow()

    private val _answersCount = MutableStateFlow<Result<CorrectAnswersStats>?>(null)
    val correctAnswersCount get() = _answersCount.asStateFlow()

    suspend fun getMostAnsweredCategories(
        token: String?,
        userUUID: UUID?,
    ) = wrapAsResult {
        statsRepository.getMostAnsweredCategories(
            token = token,
            userUUID = userUUID,
            count = CATEGORIES_COUNT,
        )
    }.also { _categoryStats.value = it }

    suspend fun getCorrectAnswersCount(
        token: String?,
        userUUID: UUID?,
    ) = wrapAsResult {
        statsRepository.getCorrectAnswersCount(
            token = token,
            userUUID = userUUID,
        )
    }.also { _answersCount.value = it }

    override fun onCleared() {
        super.onCleared()
        statsRepository.closeHttpClient()
        scope.close()
    }

    companion object {
        const val CATEGORIES_COUNT = 3

        val DEFAULT_CORRECT_STATS = CorrectAnswersStats(
            correctAnswers = 0,
            allAnswers = 0,
        )
    }
}