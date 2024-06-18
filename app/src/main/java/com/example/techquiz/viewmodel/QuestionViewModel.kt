package com.example.techquiz.viewmodel

import androidx.lifecycle.ViewModel
import com.example.techquiz.data.domain.Category
import com.example.techquiz.data.domain.Question
import com.example.techquiz.data.repository.QuestionRepository
import com.example.techquiz.util.wrapAsResult
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asSharedFlow
import kotlinx.coroutines.flow.asStateFlow
import org.koin.core.component.KoinScopeComponent
import org.koin.core.component.createScope
import org.koin.core.component.inject
import org.koin.core.scope.Scope
import kotlin.time.Duration.Companion.seconds

class QuestionViewModel : ViewModel(), KoinScopeComponent {
    override val scope: Scope by lazy { createScope(this) }

    private val questionRepository: QuestionRepository by inject()

    private lateinit var questionIterator: Iterator<IndexedValue<Question>>
    private val _question = MutableSharedFlow<Result<Question>>()
    val question
        get() = _question.asSharedFlow()

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
                _question.emit(Result.failure(it))
            }
        )
    }

    fun isQuestionLast() =
        ::questionIterator.isInitialized && !questionIterator.hasNext()

    suspend fun nextQuestion() {
        if (!isQuestionLast()) {
            val questionWithIndex = questionIterator.next()
            _question.emit(Result.success(questionWithIndex.value))
            _questionNumber.value = questionWithIndex.index + 1
        }
    }

    override fun onCleared() {
        super.onCleared()
        questionRepository.closeHttpClient()
        scope.close()
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