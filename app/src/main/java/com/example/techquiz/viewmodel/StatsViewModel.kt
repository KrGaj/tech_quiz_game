package com.example.techquiz.viewmodel

import androidx.lifecycle.ViewModel
import com.example.techquiz.data.dto.response.stats.CategoryStats
import com.example.techquiz.data.dto.response.stats.CorrectAnswersStats
import com.example.techquiz.data.repository.StatsRepository
import com.example.techquiz.util.wrapAsResult
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import java.util.UUID

class StatsViewModel(
    private val statsRepository: StatsRepository,
) : ViewModel() {
    private val _categoryStats = MutableStateFlow(Result.success(emptyList<CategoryStats>()))
    val categoryStats get() = _categoryStats.asStateFlow()

    private val _answersCount = MutableStateFlow(Result.success(DEFAULT_CORRECT_STATS))
    val correctAnswersCount get() = _answersCount.asStateFlow()

    suspend fun getMostAnsweredCategories(
        token: String?,
        userUUID: UUID?,
    ) {
        _categoryStats.value = wrapAsResult {
            statsRepository.getMostAnsweredCategories(
                token = token,
                userUUID = userUUID,
                count = CATEGORIES_COUNT,
            )
        }
    }

    suspend fun getCorrectAnswersCount(
        token: String?,
        userUUID: UUID?,
    ) {
        _answersCount.value = wrapAsResult {
            statsRepository.getCorrectAnswersCount(
                token = token,
                userUUID = userUUID,
            )
        }
    }

    companion object {
        private const val CATEGORIES_COUNT = 3

        val DEFAULT_CORRECT_STATS = CorrectAnswersStats(
            correctAnswers = 0,
            allAnswers = 0,
        )
    }
}