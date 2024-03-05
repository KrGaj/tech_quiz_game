package com.example.techquiz.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.techquiz.data.domain.AnsweredQuestionsCountStats
import com.example.techquiz.data.domain.CategoryStats
import com.example.techquiz.data.domain.CorrectAnswersStats
import com.example.techquiz.data.repository.StatsRepository
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

class StatsViewModel(
    private val statsRepository: StatsRepository,
) : ViewModel() {
    private val _categoryStats = MutableStateFlow(emptyList<CategoryStats>())
    val categoryStats get() = _categoryStats.asStateFlow()

    private val _answeredQuestionsCount = MutableStateFlow(DEFAULT_COUNT_STATS)
    val answeredQuestionsCount get() = _answeredQuestionsCount.asStateFlow()

    private val _answersCount = MutableStateFlow(DEFAULT_CORRECT_STATS)
    val correctAnswersCount get() = _answersCount.asStateFlow()

    fun getMostAnsweredCategories() {
        viewModelScope.launch {
            _categoryStats.value =
                statsRepository.getMostAnsweredCategories(CATEGORIES_COUNT)
        }
    }

    fun getAnsweredQuestionsCount() {
        viewModelScope.launch {
            _answeredQuestionsCount.value =
                statsRepository.getAnsweredQuestionsCount()
        }
    }

    fun getCorrectAnswersCount() {
        viewModelScope.launch {
            _answersCount.value =
                statsRepository.getCorrectAnswersCount()
        }
    }

    private companion object {
        private const val CATEGORIES_COUNT = 3

        private val DEFAULT_COUNT_STATS = AnsweredQuestionsCountStats(
            questionsAnswered = 0,
            allQuestions = 0,
        )

        private val DEFAULT_CORRECT_STATS = CorrectAnswersStats(
            correctAnswers = 0,
            allAnswers = 0,
        )
    }
}