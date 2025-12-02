package com.example.techquiz.data.repository

import com.example.techquiz.data.domain.Category
import com.example.techquiz.data.domain.AnswerOption
import com.example.techquiz.data.domain.Question
import com.example.techquiz.data.dto.response.QuestionResDTO
import com.example.techquiz.data.resources.Questions
import io.ktor.client.HttpClient
import io.ktor.client.call.body
import io.ktor.client.plugins.resources.get

class QuestionRepositoryDefault(
    private val httpClient: HttpClient,
) : QuestionRepository {
    override suspend fun getRandomQuestions(
        category: Category,
        quantity: Int,
    ): Result<List<Question>> = Result.runCatching {
        val response = httpClient.get(
            Questions(
                category = category.name,
                limit = quantity,
            )
        )

        val responseBody: List<QuestionResDTO> = response.body()
        val questions = mapQuestionDtoToDomainQuestion(responseBody)

        return@runCatching questions.shuffled()
    }

    private fun mapQuestionDtoToDomainQuestion(
        responseBody: List<QuestionResDTO>
    ) = responseBody.map { question ->
        val answerOptions = question.answers.asSequence()
            .zip(question.correctAnswers.asSequence())
            .filter { it.first.value != null }
            .associate {
                it.first.value as String to it.second.value
            }.map {
                AnswerOption(
                    text = it.key,
                    isCorrect = it.value,
                )
            }

        Question(
            id = question.id,
            category = Category(question.category),
            text = question.questionText,
            options = answerOptions.shuffled(),
        )
    }
}
