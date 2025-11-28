package com.example.techquiz.data

import com.example.techquiz.data.domain.Category
import com.example.techquiz.data.domain.Question
import com.example.techquiz.data.repository.QuestionRepository
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update

class QuestionFlow(
    private val questionRepository: QuestionRepository,
) {
    private lateinit var questionIterator: Iterator<IndexedValue<Question>>

    private val _state = MutableStateFlow(State())
    val state get() = _state.asStateFlow()

    suspend fun loadQuestions(
        category: Category,
        quantity: Int,
    ) {
        val questions = questionRepository.getRandomQuestions(
            category,
            quantity,
        )

        _state.update { qfState ->
            questions.fold(
                onSuccess = { getFirstQuestion(it) },
                onFailure = { qfState.copy(error = it) }
            )
        }
    }

    private fun getFirstQuestion(
        questions: List<Question>,
    ): State {
        questionIterator = questions.iterator().withIndex()

        return if (hasNextQuestion()) {
            produceNextQuestionState()
        } else {
            State(isCategoryEmpty = true)
        }
    }

    fun nextQuestion() {
        if (_state.value.hasNextQuestion) {
            _state.value = produceNextQuestionState()
        }
    }

    private fun produceNextQuestionState(): State {
        val nextIndexedQuestion = questionIterator.next()
        val question = nextIndexedQuestion.value
        val questionNumber = nextIndexedQuestion.index + 1

        return State(
            currentQuestion = question,
            questionNumber = questionNumber,
            hasNextQuestion = hasNextQuestion(),
        )
    }

    private fun hasNextQuestion() =
        ::questionIterator.isInitialized && questionIterator.hasNext()


    data class State(
        val isCategoryEmpty: Boolean = false,
        val currentQuestion: Question = Question(),
        val questionNumber: Int = 0,
        val hasNextQuestion: Boolean = false,
        val error: Throwable? = null,
    )
}