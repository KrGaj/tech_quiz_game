package com.example.techquiz.data

import com.example.techquiz.data.domain.AnswerOption
import com.example.techquiz.data.domain.Question
import com.example.techquiz.data.domain.UserAnswer
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update

class UserAnswersCollector {
    private val _state = MutableStateFlow(State())
    val state get() = _state.asStateFlow()

    fun onOptionClick(
        option: AnswerOption,
    ) = _state.update {
        if (it.userAnswers.isEmpty()) {
            return@update it
        }

        val currentItem = it.userAnswers.last()

        val updatedSelected = currentItem.selectedOptions
            .toMutableList()
            .apply {
                if (!remove(option)) add(option)
            }
        val updatedAnswer = currentItem.copy(
            selectedOptions = updatedSelected,
        )
        val result = it.userAnswers.toMutableList().apply {
            this[lastIndex] = updatedAnswer
        }

        return@update it.copy(userAnswers = result)
    }

    fun addQuestion(
        question: Question,
    ) = _state.update { state ->
        if (!shouldAddQuestion(question, state.userAnswers)) {
            return@update state
        }

        val newItem = UserAnswer(
            question = question,
        )
        val updatedAnswers = state.userAnswers
            .toMutableList()
            .also { it.add(newItem) }
        return@update state.copy(
            userAnswers = updatedAnswers,
        )
    }

    private fun shouldAddQuestion(
        question: Question,
        userAnswers: List<UserAnswer>,
    ) = userAnswers.none { it.question == question }
            && question != Question()

    data class State(
        val userAnswers: List<UserAnswer> = emptyList(),
    )
}
