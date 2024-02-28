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
    override suspend fun insertAnswers(
        answers: List<GivenAnswer>,
    ) {
        val answersDTO = answers.map {
            val questionDTO = QuestionDTO(
                id = it.question.id.toLong(),
                category = it.question.category,
            )

            GivenAnswerDTO(
                userUUID = UUID(0, 0),
                question = questionDTO,
                isCorrect = it.correct,
            )
        }

        httpClient.post(GivenAnswerRes()) {
            setBody(answersDTO)
        }
    }
}