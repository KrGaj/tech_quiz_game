package com.example.techquiz.data

import com.example.techquiz.data.domain.PossibleAnswer
import com.example.techquiz.data.domain.Question
import com.example.techquiz.data.domain.QuizResult
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update

class SelectedAnswersCollector {
    private val _state = MutableStateFlow(State())
    val state get() = _state.asStateFlow()

    fun onAnswerClick(
        answer: PossibleAnswer,
    ) {
        val updatedSelectedAnswers = _state.value.selectedAnswers
            .toMutableList()
            .apply {
                if (!remove(answer)) add(answer)
            }

        _state.update { it.copy(selectedAnswers = updatedSelectedAnswers) }
    }

    fun confirmAnswer(
        question: Question,
    ) {
        val isAnswerCorrect = question.answers
            .filter { it.isCorrect } == state.value.selectedAnswers

        val confirmedAnswers = _state.value.confirmedAnswers
            .toMutableList()

        confirmedAnswers.add(
            QuizResult(
                question = question,
                givenAnswers = state.value.selectedAnswers,
                isAnsweredCorrectly = isAnswerCorrect,
            )
        )

        _state.update { State(confirmedAnswers = confirmedAnswers) }
    }

    fun reset() {
        _state.value = State()
    }

    data class State(
        val selectedAnswers: List<PossibleAnswer> = emptyList(),
        val confirmedAnswers: List<QuizResult> = emptyList(),
    )
}
