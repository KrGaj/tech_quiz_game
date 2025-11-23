package com.example.techquiz.data

import com.example.techquiz.data.domain.Category
import com.example.techquiz.data.domain.GivenAnswer
import com.example.techquiz.data.domain.Question
import com.example.techquiz.data.repository.GivenAnswerRepository
import com.example.techquiz.data.repository.QuestionRepository
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlin.uuid.ExperimentalUuidApi
import kotlin.uuid.Uuid

class QuestionFlow(
    private val questionRepository: QuestionRepository,
    private val givenAnswerRepository: GivenAnswerRepository,
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

    @OptIn(ExperimentalUuidApi::class)
    suspend fun sendQuestionGivenAnswers(
        answers: List<GivenAnswer>,
        userUuid: Uuid,
    ) {
        val result = Result.runCatching {
            givenAnswerRepository.insertAnswers(
                userUuid = userUuid,
                answers = answers,
            )
        }

        _state.value = State(
            error = result.exceptionOrNull(),
        )
    }


    data class State(
        val isCategoryEmpty: Boolean = false,
        val currentQuestion: Question = Question(),
        val questionNumber: Int = 0,
        val hasNextQuestion: Boolean = false,
        val error: Throwable? = null,
    )
}