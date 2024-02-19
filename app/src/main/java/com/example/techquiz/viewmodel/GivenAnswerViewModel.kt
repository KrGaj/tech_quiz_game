package com.example.techquiz.viewmodel

import androidx.lifecycle.ViewModel
import com.example.techquiz.data.domain.GivenAnswer
import com.example.techquiz.data.domain.QuizResult
import com.example.techquiz.data.repository.GivenAnswerRepository
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow

class GivenAnswerViewModel(
    private val givenAnswerRepository: GivenAnswerRepository,
) : ViewModel() {
    private val _answerAddResult = MutableStateFlow<Result<Int>?>(null)
    val answerAddResult
        get() = _answerAddResult.asStateFlow()

    private val _quizResults = mutableListOf<QuizResult>()
    val quizResults get() = _quizResults.toList()

    suspend fun addAnswer(answer: GivenAnswer) {
        try {
            givenAnswerRepository.insertAnswer(answer)
            _quizResults.add(QuizResult(answer.question.text, answer.correct))
            _answerAddResult.emit(Result.success(answer.question.id))
        } catch (ex: Exception) {
            _answerAddResult.emit(Result.failure(ex))
        }
    }
}