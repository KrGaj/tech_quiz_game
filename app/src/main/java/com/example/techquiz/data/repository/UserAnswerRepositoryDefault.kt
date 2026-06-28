package com.example.techquiz.data.repository

import com.example.techquiz.data.remote.client.BackendApiClient
import com.example.techquiz.data.remote.dto.answers_api.CategoryDTO
import com.example.techquiz.data.remote.dto.answers_api.QuestionDTO
import com.example.techquiz.data.remote.dto.answers_api.UserAnswerDTO
import com.example.techquiz.domain.models.UserAnswer
import com.example.techquiz.domain.repository.UserAnswerRepository
import kotlin.uuid.ExperimentalUuidApi
import kotlin.uuid.Uuid

class UserAnswerRepositoryDefault(
    private val apiClient: BackendApiClient,
) : UserAnswerRepository {
    @OptIn(ExperimentalUuidApi::class)
    override suspend fun insertAnswers(
        userUuid: Uuid,
        answers: List<UserAnswer>,
    ): Result<Unit> = Result.runCatching {
        val answersDTO = answers.map {
            val questionDTO = QuestionDTO(
                text = it.question.text,
                category = CategoryDTO(
                    name = it.question.category.name,
                ),
            )

            UserAnswerDTO(
                userUuid = userUuid,
                question = questionDTO,
                isCorrect = it.isCorrect,
            )
        }

        apiClient.addUserAnswers(answersDTO)
    }
}
