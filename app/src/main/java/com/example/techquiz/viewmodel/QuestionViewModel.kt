package com.example.techquiz.viewmodel

import androidx.lifecycle.ViewModel
import com.example.techquiz.data.domain.Category
import com.example.techquiz.data.domain.Question
import com.example.techquiz.data.repository.QuestionRepository
import com.example.techquiz.util.wrapAsResult
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlin.time.Duration.Companion.seconds

class QuestionViewModel(
    private val questionRepository: QuestionRepository,
) : ViewModel() {
    private lateinit var questionIterator: Iterator<IndexedValue<Question>>
    private val _question = MutableStateFlow(
        value = Result.success(DEFAULT_QUESTION),
    )
    val question
        get() = _question.asStateFlow()

    private val _questionNumber = MutableStateFlow(0)
    val questionNumber
        get() = _questionNumber.asStateFlow()

    suspend fun fetchQuestions(category: Category) {
        val result = wrapAsResult {
            questionRepository.getRandomQuestions(
                quantity = QUESTION_COUNT,
                category = category,
            )
        }

        result.fold(
            onSuccess = {
                questionIterator = it.iterator().withIndex()
                nextQuestion()
            },
            onFailure = {
                _question.value = Result.failure(it)
            }
        )
    }

    fun isQuestionLast() =
        ::questionIterator.isInitialized && !questionIterator.hasNext()

    fun nextQuestion() {
        if (!isQuestionLast()) {
            val questionWithIndex = questionIterator.next()
            _question.value = Result.success(questionWithIndex.value)
            _questionNumber.value = questionWithIndex.index + 1
        }
    }

    companion object {
        private const val QUESTION_COUNT = 5
        val TIMEOUT = 30.seconds
        val DEFAULT_QUESTION = Question(
            id = 0,
            category = Category(name = "0"),
            text = "Questions not loaded yet",
            answers = emptyList(),
        )
    }
}