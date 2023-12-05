package com.example.techquiz.data.repository

import com.example.techquiz.data.database.dao.StatsDao
import com.example.techquiz.data.domain.AnsweredQuestionsCountStats
import com.example.techquiz.data.domain.Category
import com.example.techquiz.data.domain.CategoryStats
import com.example.techquiz.data.domain.CorrectAnswersStats

class StatsRepositoryDefault(
    private val statsDao: StatsDao,
) : StatsRepository {
    override suspend fun getMostAnsweredCategories(
        count: Int,
    ): List<CategoryStats> {
        val categoryEntities = statsDao.getMostAnsweredCategories(count)

        val categories = categoryEntities.map {
            CategoryStats(
                Category(
                    it.key.id,
                    it.key.name,
                ),
                it.value,
            )
        }

        return categories
    }

    override suspend fun getAnsweredQuestionsCount(): AnsweredQuestionsCountStats {
        val rawStats = statsDao.getAnsweredQuestionsCount().entries.first()

        return AnsweredQuestionsCountStats(
            questionsAnswered = rawStats.key,
            allQuestions = rawStats.value,
        )
    }

    override suspend fun getCorrectAnswersCount(): CorrectAnswersStats {
        val rawStats = statsDao.getCorrectAnswersCount().entries.first()

        return CorrectAnswersStats(
            correctAnswers = rawStats.key,
            allAnswers = rawStats.value,
        )
    }
}
