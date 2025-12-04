package com.example.techquiz.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.techquiz.data.QuestionFlow
import com.example.techquiz.data.Timer
import com.example.techquiz.data.UserAnswersCollector
import com.example.techquiz.data.domain.AnswerOption
import com.example.techquiz.data.domain.Category
import com.example.techquiz.data.domain.Question
import com.example.techquiz.data.repository.UserAnswerRepository
import com.example.techquiz.data.repository.UserDataStoreRepository
import com.example.techquiz.ui.screen.QuestionScreenState
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch
import kotlin.time.Duration.Companion.seconds
import kotlin.uuid.ExperimentalUuidApi

class QuestionViewModel2(
    private val userAnswerRepository: UserAnswerRepository,
    private val userDataStoreRepository: UserDataStoreRepository,
    private val questionFlow: QuestionFlow,
    private val timer: Timer = Timer(),
    private val userAnswersCollector: UserAnswersCollector = UserAnswersCollector(),
    private val category: Category,
) : ViewModel() {
    private val apiCallState = MutableStateFlow(ApiCallState())

    private val questionFlowState get() = questionFlow.state

    init {
        startQuestionFlow(
            category = category,
        )
    }

    val uiState = combine(
        apiCallState,
        questionFlow.state,
        userAnswersCollector.state,
        timer.timeLeft,
    ) { acState, qfState, collectorState, timeLeft ->
        return@combine QuestionScreenState(
            currentQuestion = qfState.currentQuestion,
            questionNumber = qfState.questionNumber,
            selectedAnswers = collectorState.userAnswers.lastOrNull()
                ?.selectedOptions ?: emptyList(),
            userAnswers = collectorState.userAnswers,
            timeLeft = timeLeft,
            isLoading = acState.isLoading,
            isSendingAnswers = acState.isSendingAnswers,
            error = listOf(qfState.error, acState.error).firstOrNull { it != null },
        )
    }.stateIn(
        scope = viewModelScope,
        started = SharingStarted.Eagerly,
        initialValue = QuestionScreenState(),
    )

    fun startQuestionFlow(
        category: Category,
    ) {
        apiCallState.value = ApiCallState(isLoading = true)

        viewModelScope.launch {
            questionFlow.loadQuestions(
                category = category,
                quantity = QUESTION_COUNT,
            )

            apiCallState.value = ApiCallState(isLoading = false)

            timer.takeIf { uiState.value.error == null }
                ?.start(
                    timeout = TIMEOUT,
                    ::onTimeout,
                )
        }
    }

    fun onNextQuestionClick() {
        apiCallState.value = ApiCallState(isLoading = true)

        timer.clear()
        questionFlow.nextQuestion()

        apiCallState.value = ApiCallState(isLoading = false)

        timer.start(
            timeout = TIMEOUT,
            onTimeout = ::onTimeout,
        )
    }

    private suspend fun onTimeout() =
        if (questionFlowState.value.hasNextQuestion) {
            onNextQuestionClick()
        } else {
            onSendAnswersClick()
        }

    @OptIn(ExperimentalUuidApi::class)
    fun onSendAnswersClick() {
        apiCallState.value = ApiCallState(isSendingAnswers = true)
        timer.clear()

        viewModelScope.launch {
            val user = userDataStoreRepository.userFlow.first()
            val result = Result.runCatching {
                userAnswerRepository.insertAnswers(
                    userUuid = user.userUuid,
                    answers = userAnswersCollector.state.value.userAnswers,
                )
            }

            apiCallState.value = ApiCallState(
                isSendingAnswers = false,
                error = result.exceptionOrNull(),
            )
        }
    }

    fun onAnswerOptionClick(
        option: AnswerOption,
        question: Question,
    ) = userAnswersCollector.onOptionClick(
        option = option,
        question = question,
    )


    companion object {
        private const val QUESTION_COUNT = 5
        val TIMEOUT = 30.seconds
    }
}

private data class ApiCallState(
    val isLoading: Boolean = false,
    val isSendingAnswers: Boolean = false,
    val error: Throwable? = null,
)
