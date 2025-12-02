package com.example.techquiz.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.techquiz.data.QuestionFlow
import com.example.techquiz.data.SelectedAnswersCollector
import com.example.techquiz.data.Timer
import com.example.techquiz.data.domain.Category
import com.example.techquiz.data.domain.PossibleAnswer
import com.example.techquiz.data.repository.GivenAnswerRepository
import com.example.techquiz.data.repository.UserDataStoreRepository
import com.example.techquiz.ui.screen.QuestionScreenState
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch
import kotlin.time.Duration.Companion.seconds
import kotlin.uuid.ExperimentalUuidApi

class QuestionViewModel2(
    private val givenAnswerRepository: GivenAnswerRepository,
    private val userDataStoreRepository: UserDataStoreRepository,
    private val questionFlow: QuestionFlow,
    private val timer: Timer = Timer(),
    private val selectedAnswersCollector: SelectedAnswersCollector = SelectedAnswersCollector(),
) : ViewModel() {
    private val apiCallState = MutableStateFlow(ApiCallState())

    private val questionFlowState get() = questionFlow.state

    init {
        observeCurrentQuestion()
    }

    val uiState = combine(
        apiCallState,
        questionFlow.state,
        selectedAnswersCollector.state,
        timer.timeLeft,
    ) { acState, qfState, collectorState, timeLeft ->
        return@combine QuestionScreenState(
            currentQuestion = qfState.currentQuestion,
            questionNumber = qfState.questionNumber,
            selectedAnswers = collectorState.givenAnswers.lastOrNull()
                ?.selectedPossibleAnswers ?: emptyList(),
            givenAnswers = collectorState.givenAnswers,
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

    private fun observeCurrentQuestion() {
        viewModelScope.launch(Dispatchers.Default) {
            questionFlow.state.collect {
                selectedAnswersCollector.addQuestion(
                    it.currentQuestion,
                )
            }
        }
    }

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
                givenAnswerRepository.insertAnswers(
                    userUuid = user.userUuid,
                    answers = selectedAnswersCollector.state.value.givenAnswers,
                )
            }

            apiCallState.value = ApiCallState(
                isSendingAnswers = false,
                error = result.exceptionOrNull(),
            )
        }
    }

    fun onPossibleAnswerClick(
        answer: PossibleAnswer,
    ) = selectedAnswersCollector.onAnswerClick(answer)


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
