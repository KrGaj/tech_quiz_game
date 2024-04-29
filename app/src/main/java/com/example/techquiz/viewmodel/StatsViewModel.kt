package com.example.techquiz.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.techquiz.data.dto.response.stats.CategoryStats
import com.example.techquiz.data.dto.response.stats.CorrectAnswersStats
import com.example.techquiz.data.repository.StatsRepository
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import java.util.UUID

class StatsViewModel(
    private val statsRepository: StatsRepository,
) : ViewModel() {
    private val _categoryStats = MutableStateFlow(emptyList<CategoryStats>())
    val categoryStats get() = _categoryStats.asStateFlow()

    private val _answersCount = MutableStateFlow(DEFAULT_CORRECT_STATS)
    val correctAnswersCount get() = _answersCount.asStateFlow()

    fun getMostAnsweredCategories(
        token: String?,
        userUUID: UUID?,
    ) {
        viewModelScope.launch {
            _categoryStats.value =
                statsRepository.getMostAnsweredCategories(
                    token = token,
                    userUUID = userUUID,
                    count = CATEGORIES_COUNT,
                )
        }
    }

    fun getCorrectAnswersCount(
        token: String?,
        userUUID: UUID?,
    ) {
        viewModelScope.launch {
            _answersCount.value =
                statsRepository.getCorrectAnswersCount(
                    token = token,
                    userUUID = userUUID,
                )
        }
    }

    private companion object {
        private const val CATEGORIES_COUNT = 3

        private val DEFAULT_CORRECT_STATS = CorrectAnswersStats(
            correctAnswers = 0,
            allAnswers = 0,
        )
    }
}