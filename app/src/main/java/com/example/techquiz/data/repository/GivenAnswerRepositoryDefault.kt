package com.example.techquiz.data.repository

import com.example.techquiz.data.database.dao.AnswerDao
import com.example.techquiz.data.database.entity.AnswerEntity
import com.example.techquiz.data.domain.GivenAnswer

class GivenAnswerRepositoryDefault(
    private val answerDao: AnswerDao,
) : GivenAnswerRepository {
    override suspend fun insertAnswer(
        answer: GivenAnswer,
    ) = answerDao.insertAnswer(
        AnswerEntity(
            question = answer.question.id,
            correct = answer.correct,
        )
    )
}