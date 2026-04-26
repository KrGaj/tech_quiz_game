package com.example.techquiz.data.repository

import com.example.techquiz.data.domain.Category
import com.example.techquiz.data.remote.client.QuizApiClient
import com.example.techquiz.data.remote.dto.quiz_api.QuestionsDTO
import com.example.techquiz.domain.models.AnswerOption
import com.example.techquiz.domain.models.Question
import com.example.techquiz.domain.repository.CategoryRepository
import com.example.techquiz.domain.repository.QuestionRepository
import org.koin.core.annotation.Factory

@Factory
class QuestionRepositoryDefault(
    private val apiClient: QuizApiClient,
    private val categoryRepository: CategoryRepository,
) : QuestionRepository {
    override suspend fun getRandomQuestions(
        category: Category,
        quantity: Int,
    ): Result<List<Question>> = Result.runCatching {
        val response = apiClient.getQuestions(
            category = category.id,
            amount = quantity,
        )

        val questions = mapQuestionDtoToDomainQuestion(response)

        return@runCatching questions.shuffled()
    }

    private suspend fun mapQuestionDtoToDomainQuestion(
        responseBody: QuestionsDTO
    ) = responseBody.content.map { question ->
        val answerOptions = question.incorrectAnswers.map {
            AnswerOption(
                text = it,
                isCorrect = false,
            )
        } + AnswerOption(
            text = question.correctAnswer,
            isCorrect = true,
        )

        val category = categoryRepository.getAllCategories()
            .first { it.name == question.category }

        Question(
            category = category,
            text = question.questionText,
            options = answerOptions.shuffled(),
        )
    }
}
