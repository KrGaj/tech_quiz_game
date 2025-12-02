package com.example.techquiz.data

import com.example.techquiz.data.domain.PossibleAnswer
import com.example.techquiz.data.domain.Question
import com.example.techquiz.data.domain.GivenAnswer
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update

class SelectedAnswersCollector {
    private val _state = MutableStateFlow(State())
    val state get() = _state.asStateFlow()

    fun onAnswerClick(
        answer: PossibleAnswer,
    ) = _state.update {
        if (it.givenAnswers.isEmpty()) {
            return@update it
        }

        val currentItem = it.givenAnswers.last()

        val updatedSelectedAnswers = currentItem.selectedPossibleAnswers
            .toMutableList()
            .apply {
                if (!remove(answer)) add(answer)
            }
        val updatedGivenAnswer = currentItem.copy(
            selectedPossibleAnswers = updatedSelectedAnswers,
        )
        val result = it.givenAnswers
            .toMutableList().apply {
                this[lastIndex] = updatedGivenAnswer
            }

        return@update it.copy(givenAnswers = result)
    }

    fun addQuestion(
        question: Question,
    ) = _state.update { state ->
        if (state.givenAnswers.any { it.question == question }) {
            return@update state
        }

        val newItem = GivenAnswer(
            question = question,
        )
        val updatedGivenAnswers = state.givenAnswers
            .toMutableList()
            .also { it.add(newItem) }
        return@update state.copy(
            givenAnswers = updatedGivenAnswers,
        )
    }

    data class State(
        val givenAnswers: List<GivenAnswer> = emptyList(),
    )
}
