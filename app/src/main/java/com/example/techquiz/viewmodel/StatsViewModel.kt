package com.example.techquiz.viewmodel

import androidx.lifecycle.ViewModel
import com.example.techquiz.data.dto.response.stats.CategoryStats
import com.example.techquiz.data.dto.response.stats.CorrectAnswersStats
import com.example.techquiz.data.repository.StatsRepository
import com.example.techquiz.data.repository.UserDataStoreRepository
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.first

class StatsViewModel(
    private val statsRepository: StatsRepository,
    private val userDataStoreRepository: UserDataStoreRepository,
) : ViewModel() {
    private val _categoryStats = MutableStateFlow<Result<List<CategoryStats>>?>(null)
    val categoryStats get() = _categoryStats.asStateFlow()

    private val _answersCount = MutableStateFlow<Result<CorrectAnswersStats>?>(null)
    val correctAnswersCount get() = _answersCount.asStateFlow()

    suspend fun getMostAnsweredCategories() {
        val userPreferences = userDataStoreRepository.userFlow.first()

        val statsResult = statsRepository.getMostAnsweredCategories(
            userUUID = userPreferences.userUUID,
            count = CATEGORIES_COUNT,
        )

        _categoryStats.value = statsResult
    }

    suspend fun getCorrectAnswersCount() {
        val userPreferences = userDataStoreRepository.userFlow.first()

        val statsResult = statsRepository.getCorrectAnswersCount(
            userUUID = userPreferences.userUUID,
        )

        _answersCount.value = statsResult
    }

    companion object {
        const val CATEGORIES_COUNT = 3

        val DEFAULT_CORRECT_STATS = CorrectAnswersStats(
            correctAnswers = 0,
            allAnswers = 0,
        )
    }
}