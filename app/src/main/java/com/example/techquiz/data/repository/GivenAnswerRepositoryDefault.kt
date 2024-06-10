package com.example.techquiz.data.repository

import com.example.techquiz.data.domain.GivenAnswer
import com.example.techquiz.data.dto.request.GivenAnswerDTO
import com.example.techquiz.data.dto.request.QuestionReqDTO
import com.example.techquiz.data.resources.GivenAnswers
import com.example.techquiz.util.postWithToken
import io.ktor.client.HttpClient
import io.ktor.client.request.setBody
import java.util.UUID

// TODO improve
class GivenAnswerRepositoryDefault(
    private val httpClient: HttpClient,
) : GivenAnswerRepository {
    override suspend fun insertAnswers(
        token: String?,
        userUUID: UUID?,
        answers: List<GivenAnswer>,
    ) {
        val answersDTO = answers.map {
            val questionDTO = QuestionReqDTO(
                id = it.question.id.toLong(),
                category = it.question.category,
            )

            GivenAnswerDTO(
                userUUID = userUUID,
                question = questionDTO,
                isCorrect = it.correct,
            )
        }

        httpClient.postWithToken(
            resource = GivenAnswers(),
            token = token,
        ) {
            setBody(answersDTO)
        }
    }

    override fun closeHttpClient() {
        httpClient.close()
    }
}