package com.example.techquiz.app.ui.question

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.techquiz.app.ui.mapper.toQuestionDataUiState
import com.example.techquiz.data.Timer
import com.example.techquiz.data.UserAnswersCollector
import com.example.techquiz.data.domain.AnswerOption
import com.example.techquiz.data.domain.Category
import com.example.techquiz.data.domain.Question
import com.example.techquiz.data.repository.QuestionRepository
import com.example.techquiz.data.repository.UserAnswerRepository
import com.example.techquiz.data.repository.UserDataStoreRepository
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.onStart
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import kotlin.time.Duration
import kotlin.time.Duration.Companion.seconds
import kotlin.uuid.ExperimentalUuidApi

class QuestionViewModel(
    private val category: Category,
    private val questionRepository: QuestionRepository,
    private val userAnswerRepository: UserAnswerRepository,
    private val userDataStoreRepository: UserDataStoreRepository,
    private val userAnswersCollector: UserAnswersCollector = UserAnswersCollector(),
    private val timer: Timer = Timer(),
    private val timeout: Duration = 30.seconds,
) : ViewModel() {
    private val _viewModelState = MutableStateFlow(QuestionsState())

    val uiState = combine(
        _viewModelState,
        userAnswersCollector.state,
        timer.timeLeft,
    ) { vmState, collectorState, timeLeft ->
        QuestionUiState(
            question = vmState.questions.firstOrNull()?.toQuestionDataUiState(
                questionNumber = vmState.questionNumber,
                isLast = vmState.hasOneQuestion,
            ),
            selectedAnswers = collectorState.userAnswers.lastOrNull()
                ?.selectedOptions ?: emptyList(),
            userAnswers = collectorState.userAnswers,
            timeLeft = timeLeft,
            isLoading = vmState.isLoading,
            isSendingAnswers = vmState.isSendingAnswers,
            error = vmState.error,
        )
    }.onStart {
        loadQuestions(
            questionsCount = QUESTIONS_NUM,
        )
    }.stateIn(
        scope = viewModelScope,
        started = SharingStarted.WhileSubscribed(5000L),
        initialValue = QuestionUiState(),
    )

    private fun loadQuestions(
        questionsCount: Int,
    ) {
        viewModelScope.launch {
            timer.clear()

            _viewModelState.update {
                it.copy(isLoading = true)
            }

            val result = questionRepository.getRandomQuestions(
                category = category,
                quantity = questionsCount,
            )

            _viewModelState.update { state ->
                result.fold(
                    onSuccess = {
                        state.copy(
                            questions = it,
                            questionNumber = 1,
                            isLoading = false,
                            error = null,
                        )
                    },
                    onFailure = {
                        state.copy(
                            isLoading = false,
                            error = it,
                        )
                    },
                )
            }

            timer.start(
                timeout = timeout,
                onTimeout = ::onTimeout,
            )
        }
    }

    private suspend fun onTimeout() =
        if (!_viewModelState.value.hasOneQuestion) {
            onNextQuestionClick()
        } else {
            onSendAnswersClick()
        }

    fun onAnswerOptionClick(
        option: AnswerOption,
        question: Question,
    ) = userAnswersCollector.onOptionClick(
        option = option,
        question = question,
    )

    fun onNextQuestionClick() {
        timer.clear()

        _viewModelState.update {
            popFirstQuestion(it)
        }

        timer.start(
            timeout = timeout,
            onTimeout = ::onTimeout,
        )
    }

    private fun popFirstQuestion(
        currentState: QuestionsState,
    ) = if (currentState.hasOneQuestion) {
        currentState
    } else {
        currentState.copy(
            questions = currentState.questions.drop(1),
            questionNumber = currentState.questionNumber + 1,
            isLoading = false,
            error = null,
        )
    }

    @OptIn(ExperimentalUuidApi::class)
    fun onSendAnswersClick() {
        viewModelScope.launch {
            _viewModelState.update {
                it.copy(isSendingAnswers = true)
            }
            timer.clear()

            val user = userDataStoreRepository.userFlow.first()
            val result = userAnswerRepository.insertAnswers(
                userUuid = user.userUuid,
                answers = uiState.value.userAnswers,
            )

            _viewModelState.update {
                it.copy(
                    isSendingAnswers = false,
                    error = result.exceptionOrNull(),
                )
            }
        }
    }

    companion object {
        private const val QUESTIONS_NUM = 5
    }

    private data class QuestionsState(
        val questions: List<Question> = emptyList(),
        val questionNumber: Int = 0,
        val isLoading: Boolean = false,
        val isSendingAnswers: Boolean = false,
        val error: Throwable? = null,
    ) {
        val hasOneQuestion get() = questions.size == 1
    }
}
