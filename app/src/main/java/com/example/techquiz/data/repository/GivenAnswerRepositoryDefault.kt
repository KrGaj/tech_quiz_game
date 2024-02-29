package com.example.techquiz.data.repository

import com.example.techquiz.data.domain.GivenAnswer
import com.example.techquiz.data.dto.request.GivenAnswerDTO
import com.example.techquiz.data.dto.request.QuestionReqDTO
import com.example.techquiz.data.resources.GivenAnswers
import io.ktor.client.HttpClient
import io.ktor.client.plugins.resources.post
import io.ktor.client.request.setBody
import java.util.UUID

// TODO improve
class GivenAnswerRepositoryDefault(
    private val httpClient: HttpClient,
) : GivenAnswerRepository {
    override suspend fun insertAnswers(
        answers: List<GivenAnswer>,
    ) {
        val answersDTO = answers.map {
            val questionDTO = QuestionReqDTO(
                id = it.question.id.toLong(),
                category = it.question.category,
            )

            GivenAnswerDTO(
                userUUID = UUID(0, 0),
                question = questionDTO,
                isCorrect = it.correct,
            )
        }

        httpClient.post(GivenAnswers()) {
            setBody(answersDTO)
        }
    }
}