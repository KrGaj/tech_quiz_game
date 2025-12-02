package com.example.techquiz.data.repository

import com.example.techquiz.data.domain.UserAnswer
import com.example.techquiz.data.dto.request.UserAnswerDTO
import com.example.techquiz.data.dto.request.QuestionReqDTO
import com.example.techquiz.data.resources.UserAnswers
import io.ktor.client.HttpClient
import io.ktor.client.plugins.resources.post
import io.ktor.client.request.setBody
import kotlin.uuid.ExperimentalUuidApi
import kotlin.uuid.Uuid

// TODO improve
class UserAnswerRepositoryDefault(
    private val httpClient: HttpClient,
) : UserAnswerRepository {
    @OptIn(ExperimentalUuidApi::class)
    override suspend fun insertAnswers(
        userUuid: Uuid,
        answers: List<UserAnswer>,
    ) {
        val answersDTO = answers.map {
            val questionDTO = QuestionReqDTO(
                id = it.question.id.toLong(),
                category = it.question.category,
            )

            UserAnswerDTO(
                userUuid = userUuid,
                question = questionDTO,
                isCorrect = it.isCorrect,
            )
        }

        httpClient.post(
            resource = UserAnswers(),
        ) {
            setBody(answersDTO)
        }
    }
}