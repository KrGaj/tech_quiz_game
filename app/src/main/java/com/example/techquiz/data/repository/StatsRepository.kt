package com.example.techquiz.data.repository

import com.example.techquiz.data.domain.AnsweredQuestionsCountStats
import com.example.techquiz.data.domain.CategoryStats
import com.example.techquiz.data.domain.CorrectAnswersStats

interface StatsRepository {
    suspend fun getMostAnsweredCategories(
        count: Int,
    ): List<CategoryStats>

    suspend fun getAnsweredQuestionsCount(): AnsweredQuestionsCountStats

    suspend fun getCorrectAnswersCount(): CorrectAnswersStats
}
