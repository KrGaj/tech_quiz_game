package com.example.techquiz.viewmodel

import androidx.lifecycle.ViewModel
import com.example.techquiz.data.domain.GivenAnswer
import com.example.techquiz.data.domain.QuizResult
import com.example.techquiz.data.repository.GivenAnswerRepository
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.asSharedFlow

class GivenAnswerViewModel(
    private val givenAnswerRepository: GivenAnswerRepository,
) : ViewModel() {
    private val _answerAddResult = MutableSharedFlow<Result<Unit>>()
    val answerAddResult
        get() = _answerAddResult.asSharedFlow()

    private val _quizResults = mutableListOf<QuizResult>()
    val quizResults get() = _quizResults.toList()

    suspend fun addAnswer(answer: GivenAnswer) {
        try {
            givenAnswerRepository.insertAnswer(answer)
            _quizResults.add(QuizResult(answer.question.text, answer.correct))
            _answerAddResult.emit(Result.success(Unit))
        } catch (ex: Exception) {
            _answerAddResult.emit(Result.failure(ex))
        }
    }
}