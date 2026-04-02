package com.example.techquiz.data.repository

import com.example.techquiz.data.domain.GivenAnswer
import com.example.techquiz.data.dto.request.QuestionReqDTO
import com.example.techquiz.data.remote.client.BackendApiClient
import com.example.techquiz.data.remote.dto.request.GivenAnswerDTO
import kotlin.uuid.ExperimentalUuidApi
import kotlin.uuid.Uuid

// TODO improve
class GivenAnswerRepositoryDefault(
    private val apiClient: BackendApiClient,
) : GivenAnswerRepository {
    @OptIn(ExperimentalUuidApi::class)
    override suspend fun insertAnswers(
        userUuid: Uuid?,
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

        apiClient.addUserAnswers(answersDTO)
    }
}