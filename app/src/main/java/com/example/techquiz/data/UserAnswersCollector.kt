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
        question: Question,
    ) = _state.update {
        val answerForQuestion = it.userAnswers.find {
            elem -> elem.question == question
        }

        return@update when(answerForQuestion) {
            null -> addNewAnswer(
                state = it,
                question = question,
                selectedOption = option,
            )
            else -> modifyExistingAnswer(
                state = it,
                answer = answerForQuestion,
                selectedOption = option,
            )
        }
    }

    private fun addNewAnswer(
        state: State,
        question: Question,
        selectedOption: AnswerOption,
    ): State {
        val userAnswer = UserAnswer(
            question = question,
            selectedOptions = listOf(
                selectedOption,
            )
        )
        val userAnswersUpdated = state.userAnswers.toMutableList()
            .also {
                it.add(userAnswer)
            }

        return state.copy(userAnswers = userAnswersUpdated)
    }

    private fun modifyExistingAnswer(
        state: State,
        answer: UserAnswer,
        selectedOption: AnswerOption,
    ): State {
        val answerIndex = state.userAnswers.indexOf(answer)
        val updatedSelected = answer.selectedOptions
            .toMutableList()
            .apply {
                if (!remove(selectedOption)) add(selectedOption)
            }
        val updatedAnswer = answer.copy(
            selectedOptions = updatedSelected,
        )
        val userAnswersUpdated = state.userAnswers.toMutableList()
            .apply {
                this[answerIndex] = updatedAnswer
            }

        return state.copy(userAnswers = userAnswersUpdated)
    }

    data class State(
        val userAnswers: List<UserAnswer> = emptyList(),
    )
}
