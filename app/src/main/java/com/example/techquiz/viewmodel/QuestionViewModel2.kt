package com.example.techquiz.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.techquiz.data.QuestionFlow
import com.example.techquiz.data.SelectedAnswersCollector
import com.example.techquiz.data.Timer
import com.example.techquiz.data.domain.Category
import com.example.techquiz.data.domain.GivenAnswer
import com.example.techquiz.data.domain.PossibleAnswer
import com.example.techquiz.data.domain.QuizResult
import com.example.techquiz.data.repository.GivenAnswerRepository
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
    private val givenAnswerRepository: GivenAnswerRepository,
    private val userDataStoreRepository: UserDataStoreRepository,
    private val questionFlow: QuestionFlow,
    private val timer: Timer = Timer(),
    private val selectedAnswersCollector: SelectedAnswersCollector = SelectedAnswersCollector(),
) : ViewModel() {
    private val apiCallState = MutableStateFlow(ApiCallState())

    private val questionFlowState get() = questionFlow.state
    private val collectorState = selectedAnswersCollector.state

    val uiState = combine(
        apiCallState,
        questionFlow.state,
        selectedAnswersCollector.state,
        timer.timeLeft,
    ) { acState, qfState, collectorState, timeLeft ->
        return@combine QuestionScreenState(
            currentQuestion = qfState.currentQuestion,
            questionNumber = qfState.questionNumber,
            selectedAnswers = collectorState.selectedAnswers,
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
        confirmSelectedAnswers()
        selectedAnswersCollector.reset()
        questionFlow.nextQuestion()

        apiCallState.value = ApiCallState(isLoading = false)

        timer.start(
            timeout = TIMEOUT,
            onTimeout = ::onTimeout,
        )
    }

    private fun confirmSelectedAnswers() =
        selectedAnswersCollector.confirmAnswer(
            question = uiState.value.currentQuestion,
        )

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
            val answers = collectorState.value.confirmedAnswers
                .map(::mapQuizResultToGivenAnswer)
            val result = Result.runCatching {
                givenAnswerRepository.insertAnswers(
                    userUuid = user.userUuid,
                    answers = answers,
                )
            }

            apiCallState.value = ApiCallState(
                isSendingAnswers = false,
                error = result.exceptionOrNull(),
            )
        }
    }

    private fun mapQuizResultToGivenAnswer(
        quizResult: QuizResult
    ) = GivenAnswer(
        question = quizResult.question,
        correct = quizResult.isAnsweredCorrectly,
    )

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
