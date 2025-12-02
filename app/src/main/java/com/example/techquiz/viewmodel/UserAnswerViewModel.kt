package com.example.techquiz.viewmodel

import androidx.lifecycle.ViewModel
import com.example.techquiz.data.domain.AnswerOption
import com.example.techquiz.data.domain.Question
import com.example.techquiz.data.domain.UserAnswer
import com.example.techquiz.data.repository.UserAnswerRepository
import com.example.techquiz.data.repository.UserDataStoreRepository
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.first
import kotlin.uuid.ExperimentalUuidApi

class UserAnswerViewModel(
    private val userAnswerRepository: UserAnswerRepository,
    private val userDataStoreRepository: UserDataStoreRepository,
) : ViewModel() {
    private val _selectedAnswers = MutableStateFlow(listOf<AnswerOption>())
    val selectedAnswers
        get() = _selectedAnswers.asStateFlow()

    private val _answerAddResult = MutableStateFlow<Result<Int>?>(null)
    val answerAddResult
        get() = _answerAddResult.asStateFlow()

    private val _userAnswers = mutableListOf<UserAnswer>()
    val quizResults
        get() = _userAnswers.toList()

    fun toggleAnswer(answer: AnswerOption) {
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
        val isAnswerCorrect = question.options
            .filter { it.isCorrect } == selectedAnswers.value

        _userAnswers.add(
            UserAnswer(
                question = question,
                selectedOptions = selectedAnswers.value,
                isCorrect = isAnswerCorrect,
            )
        )
    }

    @OptIn(ExperimentalUuidApi::class)
    suspend fun sendAnswers() {
        val userPreferences = userDataStoreRepository.userFlow.first()

        _answerAddResult.value = Result.runCatching {
            userAnswerRepository.insertAnswers(
                userUuid = userPreferences.userUuid,
                answers = quizResults,
            )

            quizResults.first().question.id
        }
    }
}
