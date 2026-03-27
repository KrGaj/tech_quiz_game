package com.example.techquiz.ui.screen

import com.example.techquiz.data.dto.response.stats.CategoryStats
import com.example.techquiz.data.dto.response.stats.CorrectAnswersStats

data class StatsScreenState(
    val mostAnsweredCategories: List<CategoryStats> = emptyList(),
    val correctAnswersStats: CorrectAnswersStats = CorrectAnswersStats(),
    val isLoading: Boolean = false,
    val error: Throwable? = null,
)
