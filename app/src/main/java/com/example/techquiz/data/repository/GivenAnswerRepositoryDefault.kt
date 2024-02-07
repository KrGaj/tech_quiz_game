package com.example.techquiz.data.repository

import com.example.techquiz.data.domain.GivenAnswer
import com.example.techquiz.data.dto.GivenAnswerDTO
import com.example.techquiz.data.dto.QuestionDTO
import com.example.techquiz.data.resources.GivenAnswerRes
import io.ktor.client.HttpClient
import io.ktor.client.plugins.resources.post
import io.ktor.client.request.setBody
import java.util.UUID

// TODO improve
class GivenAnswerRepositoryDefault(
    private val httpClient: HttpClient,
) : GivenAnswerRepository {
    override suspend fun insertAnswer(
        answer: GivenAnswer,
    ) {
        val questionDTO = QuestionDTO(
            id = answer.question.id.toLong(),
            category = answer.question.category,
        )

        val answerDTO = GivenAnswerDTO(
            userUUID = UUID(0, 0),
            question = questionDTO,
            isCorrect = answer.correct,
        )

        httpClient.post(GivenAnswerRes()) {
            setBody(answerDTO)
        }
    }
}