package com.example.techquiz.viewmodel

import androidx.lifecycle.ViewModel
import com.example.techquiz.data.repository.StatsRepository
import com.example.techquiz.data.repository.UserDataStoreRepository
import com.example.techquiz.ui.screen.StatsScreenState
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.update

class StatsViewModel(
    private val statsRepository: StatsRepository,
    private val userDataStoreRepository: UserDataStoreRepository,
) : ViewModel() {
    private val _uiState = MutableStateFlow(value = StatsScreenState())
    val uiState get() = _uiState.asStateFlow()

    suspend fun getStats() {
        _uiState.update { it.copy(error = null, isLoading = true) }

        getCategoriesStats()
        getCorrectAnswersStats()

        _uiState.update { it.copy(isLoading = false) }
    }

    private suspend fun getCategoriesStats() {
        val userPreferences = userDataStoreRepository.userFlow.first()

        val statsResult = statsRepository.getMostAnsweredCategories(
            userUUID = userPreferences.userUUID,
            count = CATEGORIES_COUNT,
        )

        _uiState.update { state ->
            statsResult.fold(
                onSuccess = { state.copy(mostAnsweredCategories = it) },
                onFailure = { state.copy(error = it) },
            )
        }
    }

    private suspend fun getCorrectAnswersStats() {
        val userPreferences = userDataStoreRepository.userFlow.first()

        val statsResult = statsRepository.getCorrectAnswersCount(
            userUUID = userPreferences.userUUID,
        )

        _uiState.update { state ->
            statsResult.fold(
                onSuccess = { state.copy(correctAnswersStats = it) },
                onFailure = { state.copy(error = it) },
            )
        }
    }

    companion object {
        const val CATEGORIES_COUNT = 3
    }
}