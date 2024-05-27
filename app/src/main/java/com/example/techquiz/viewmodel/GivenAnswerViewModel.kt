package com.example.techquiz.viewmodel

import androidx.lifecycle.ViewModel
import com.example.techquiz.data.domain.GivenAnswer
import com.example.techquiz.data.domain.PossibleAnswer
import com.example.techquiz.data.domain.Question
import com.example.techquiz.data.domain.QuizResult
import com.example.techquiz.data.repository.GivenAnswerRepository
import com.example.techquiz.util.wrapAsResult
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import java.util.UUID

class GivenAnswerViewModel(
    private val givenAnswerRepository: GivenAnswerRepository,
) : ViewModel() {
    private val _selectedAnswers = MutableStateFlow(listOf<PossibleAnswer>())
    val selectedAnswers
        get() = _selectedAnswers.asStateFlow()

    private val _answerAddResult = MutableStateFlow<Result<Int>?>(null)
    val answerAddResult
        get() = _answerAddResult.asStateFlow()

    private val _quizResults = mutableListOf<QuizResult>()
    val quizResults
        get() = _quizResults.toList()

    fun toggleAnswer(answer: PossibleAnswer) {
        val modifiedAnswers = _selectedAnswers.value
            .toMutableList()
            .apply {
                if (!remove(answer)) add(answer)
            }

        _selectedAnswers.value = modifiedAnswers
    }

    fun clearSelectedAnswers() {
        _selectedAnswers.value = emptyList()
    }

    fun addAnswer(question: Question) {
        val isAnswerCorrect = question.answers
            .filter { it.isCorrect } == selectedAnswers.value

        _quizResults.add(
            QuizResult(
                question = question,
                givenAnswers = selectedAnswers.value,
                isAnsweredCorrectly = isAnswerCorrect,
            )
        )
    }

    suspend fun sendAnswers(
        userUUID: UUID?,
        token: String?,
    ) {
        val answers = quizResults.map(::mapQuizResultToGivenAnswer)

        _answerAddResult.value = wrapAsResult {
            givenAnswerRepository.insertAnswers(
                token = token,
                userUUID = userUUID,
                answers = answers,
            )

            answers.first().question.id
        }
    }
}

private fun mapQuizResultToGivenAnswer(
    quizResult: QuizResult
) = GivenAnswer(
    question = quizResult.question,
    correct = quizResult.isAnsweredCorrectly,
)
