package com.example.techquiz.viewmodel

import androidx.lifecycle.ViewModel
import com.example.techquiz.data.dto.response.stats.CategoryStats
import com.example.techquiz.data.dto.response.stats.CorrectAnswersStats
import com.example.techquiz.data.repository.StatsRepository
import com.example.techquiz.data.repository.UserDataStoreRepository
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.first
import org.koin.core.component.KoinScopeComponent
import org.koin.core.component.createScope
import org.koin.core.component.inject
import org.koin.core.scope.Scope

class StatsViewModel : ViewModel(), KoinScopeComponent {
    override val scope: Scope by lazy { createScope(this) }

    private val statsRepository: StatsRepository by inject()
    private val userDataStoreRepository: UserDataStoreRepository by inject()

    private val _categoryStats = MutableStateFlow<Result<List<CategoryStats>>?>(null)
    val categoryStats get() = _categoryStats.asStateFlow()

    private val _answersCount = MutableStateFlow<Result<CorrectAnswersStats>?>(null)
    val correctAnswersCount get() = _answersCount.asStateFlow()

    suspend fun getMostAnsweredCategories() = Result.runCatching {
        val userPreferences = userDataStoreRepository.userFlow.first()

        statsRepository.getMostAnsweredCategories(
            userUUID = userPreferences.userUUID,
            count = CATEGORIES_COUNT,
        )
    }.also { _categoryStats.value = it }

    suspend fun getCorrectAnswersCount() = Result.runCatching {
        val userPreferences = userDataStoreRepository.userFlow.first()

        statsRepository.getCorrectAnswersCount(
            userUUID = userPreferences.userUUID,
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