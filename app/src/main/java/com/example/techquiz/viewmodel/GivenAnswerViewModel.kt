package com.example.techquiz.viewmodel

import androidx.lifecycle.ViewModel
import com.example.techquiz.data.domain.GivenAnswer
import com.example.techquiz.data.domain.PossibleAnswer
import com.example.techquiz.data.domain.Question
import com.example.techquiz.data.domain.QuizResult
import com.example.techquiz.data.repository.GivenAnswerRepository
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow

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
            .also {
                if (!it.remove(answer)) it.add(answer)
            }

        _selectedAnswers.value = modifiedAnswers
    }

    fun clearSelectedAnswers() {
        _selectedAnswers.value = emptyList()
    }

    suspend fun addAnswer(question: Question) {
        try {
            val answer = GivenAnswer(
                question = question,
                correct = selectedAnswers.value.all { it.isCorrect }
            )

            givenAnswerRepository.insertAnswer(answer)
            _quizResults.add(
                QuizResult(
                    question = question,
                    givenAnswers = selectedAnswers.value,
                )
            )
            _answerAddResult.value = Result.success(answer.question.id)
        } catch (ex: Exception) {
            _answerAddResult.value = Result.failure(ex)
        }
    }
}