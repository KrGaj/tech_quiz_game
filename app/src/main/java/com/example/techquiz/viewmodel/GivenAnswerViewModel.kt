package com.example.techquiz.viewmodel

import androidx.lifecycle.ViewModel
import com.example.techquiz.data.domain.GivenAnswer
import com.example.techquiz.data.domain.PossibleAnswer
import com.example.techquiz.data.domain.Question
import com.example.techquiz.data.domain.QuizResult
import com.example.techquiz.data.repository.GivenAnswerRepository
import com.example.techquiz.data.repository.UserDataStoreRepository
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.first
import kotlin.uuid.ExperimentalUuidApi

class GivenAnswerViewModel(
    private val givenAnswerRepository: GivenAnswerRepository,
    private val userDataStoreRepository: UserDataStoreRepository,
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

    @OptIn(ExperimentalUuidApi::class)
    suspend fun sendAnswers() {
        val answers = quizResults.map(::mapQuizResultToGivenAnswer)
        val userPreferences = userDataStoreRepository.userFlow.first()

        _answerAddResult.value = Result.runCatching {
            givenAnswerRepository.insertAnswers(
                userUuid = userPreferences.userUuid,
                answers = answers,
            )

            answers.first().question.id
        }
    }

    private fun mapQuizResultToGivenAnswer(
        quizResult: QuizResult
    ) = GivenAnswer(
        question = quizResult.question,
        correct = quizResult.isAnsweredCorrectly,
    )
}
