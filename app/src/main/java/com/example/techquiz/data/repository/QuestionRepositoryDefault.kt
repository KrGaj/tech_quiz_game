package com.example.techquiz.data.repository

import com.example.techquiz.data.domain.Category
import com.example.techquiz.data.domain.PossibleAnswer
import com.example.techquiz.data.domain.Question
import com.example.techquiz.data.dto.QuestionResponse
import com.example.techquiz.data.resources.Questions
import io.ktor.client.HttpClient
import io.ktor.client.call.body
import io.ktor.client.plugins.resources.get

class QuestionRepositoryDefault(
    private val httpClient: HttpClient,
) : QuestionRepository {
    override suspend fun getRandomQuestions(
        quantity: Int,
        category: Category,
    ): Result<List<Question>> =
        try {
            val response = httpClient.get(Questions(category = category.name, limit = quantity))
            val responseBody: List<QuestionResponse> = response.body()

            val questions = responseBody.map { question ->
                val possibleAnswers = question.answers.asSequence()
                    .zip(question.correctAnswers.asSequence())
                    .takeWhile { it.first.key != null }
                    .associate {
                        it.first.value as String to it.second.value as Boolean
                    }.map {
                        PossibleAnswer(
                            text = it.key,
                            isCorrect = it.value,
                        )
                    }

                Question(
                    id = question.id,
                    category = question.category,
                    text = question.questionText,
                    answers = possibleAnswers.shuffled(),
                )
            }

            Result.success(questions.shuffled())
        } catch (ex: Exception) {
            Result.failure(ex)
        }
}
