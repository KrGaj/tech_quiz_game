package com.example.techquiz.data.repository

import com.example.techquiz.data.domain.GivenAnswer
import com.example.techquiz.data.dto.request.GivenAnswerDTO
import com.example.techquiz.data.dto.request.QuestionReqDTO
import com.example.techquiz.data.resources.GivenAnswers
import io.ktor.client.HttpClient
import io.ktor.client.plugins.resources.post
import io.ktor.client.request.setBody
import kotlin.uuid.ExperimentalUuidApi
import kotlin.uuid.Uuid

// TODO improve
class GivenAnswerRepositoryDefault(
    private val httpClient: HttpClient,
) : GivenAnswerRepository {
    @OptIn(ExperimentalUuidApi::class)
    override suspend fun insertAnswers(
        userUuid: Uuid,
        answers: List<GivenAnswer>,
    ) {
        val answersDTO = answers.map {
            val questionDTO = QuestionReqDTO(
                id = it.question.id.toLong(),
                category = it.question.category,
            )

            GivenAnswerDTO(
                userUuid = userUuid,
                question = questionDTO,
                isCorrect = it.correct,
            )
        }

        httpClient.post(
            resource = GivenAnswers(),
        ) {
            setBody(answersDTO)
        }
    }
}