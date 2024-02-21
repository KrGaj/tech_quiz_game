package com.example.techquiz.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.techquiz.data.domain.Category
import com.example.techquiz.data.domain.Question
import com.example.techquiz.data.repository.QuestionRepository
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
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

    fun fetchQuestions(category: Category) {
        viewModelScope.launch {
            try {
                val questions = questionRepository.getRandomQuestions(
                    quantity = QUESTION_COUNT,
                    category = category,
                )

                questionIterator = questions.iterator().withIndex()
                nextQuestion()
            } catch (e: Exception) {
                _question.value = Result.failure(e)
            }
        }
    }

    fun isQuestionLast() = ::questionIterator.isInitialized && !questionIterator.hasNext()

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