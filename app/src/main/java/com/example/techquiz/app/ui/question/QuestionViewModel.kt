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
import com.example.techquiz.util.getHttpFailureMessage
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
    private val _sessionState = MutableStateFlow(QuestionsSessionState())

    val uiState = combine(
        _sessionState,
        userAnswersCollector.state,
        timer.timeLeft,
    ) { sessionState, collectorState, timeLeft ->
        when {
            sessionState.loadingState == LoadingState.PENDING ->
                QuestionUiState.Loading(
                    categoryName = category.name,
                )
            sessionState.answersSendingState == AnswersSendingState.PENDING ->
                QuestionUiState.SendingAnswers
            sessionState.answersSendingState == AnswersSendingState.SUCCESS ->
                QuestionUiState.AnswersSent(
                    userAnswers = collectorState.userAnswers,
                )
            sessionState.error != null ->
                QuestionUiState.Error(
                    errorMsgRes = getHttpFailureMessage(sessionState.error),
                )
            sessionState.questions.isEmpty() ->
                QuestionUiState.EmptyCategory
            else ->
                QuestionUiState.Success(
                    question = sessionState.questions.first().toQuestionDataUiState(
                        questionNumber = sessionState.questionNumber,
                        selectedOptions = collectorState.userAnswers.firstOrNull {
                            it.question == sessionState.questions.first()
                        }?.selectedOptions ?: emptyList(),
                        isLast = sessionState.hasOneQuestion,
                    ),
                    isExitDialogVisible = sessionState.showExitDialog,
                    timeLeft = timeLeft.inWholeSeconds,
                )
        }
    }.onStart {
        loadQuestions()
    }.stateIn(
        scope = viewModelScope,
        started = SharingStarted.WhileSubscribed(5000L),
        initialValue = QuestionUiState.Loading(categoryName = category.name),
    )

    private fun loadQuestions() {
        viewModelScope.launch {
            timer.clear()

            _sessionState.update {
                it.copy(loadingState = LoadingState.PENDING)
            }

            val result = questionRepository.getRandomQuestions(
                category = category,
                quantity = QUESTIONS_NUM,
            )

            _sessionState.update { state ->
                result.fold(
                    onSuccess = {
                        state.copy(
                            questions = it,
                            questionNumber = 1,
                            loadingState = LoadingState.IDLE,
                            error = null,
                        )
                    },
                    onFailure = {
                        state.copy(
                            loadingState = LoadingState.FAILURE,
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
        if (!_sessionState.value.hasOneQuestion) {
            onNextQuestionClick()
        } else {
            onSendAnswersClick()
        }

    fun setExitDialogVisibility(
        value: Boolean,
    ) = _sessionState.update {
        it.copy(showExitDialog = value)
    }

    fun onRetryClick() {
        _sessionState.value.let {
            when {
                it.loadingState == LoadingState.FAILURE -> loadQuestions()
                it.answersSendingState == AnswersSendingState.FAILURE -> onSendAnswersClick()
            }
        }
    }

    fun onAnswerOptionClick(
        option: AnswerOption,
    ) = when(uiState.value) {
        is QuestionUiState.Success if (_sessionState.value.questions.isNotEmpty()) ->
            userAnswersCollector.onOptionClick(
                option = option,
                question = _sessionState.value.questions.first(),
            )
        else -> Unit
    }

    fun onNextQuestionClick() {
        timer.clear()

        _sessionState.update {
            popFirstQuestion(it)
        }

        timer.start(
            timeout = timeout,
            onTimeout = ::onTimeout,
        )
    }

    private fun popFirstQuestion(
        currentState: QuestionsSessionState,
    ) = if (currentState.hasOneQuestion) {
        currentState
    } else {
        currentState.copy(
            questions = currentState.questions.drop(1),
            questionNumber = currentState.questionNumber + 1,
            loadingState = LoadingState.IDLE,
            error = null,
        )
    }

    @OptIn(ExperimentalUuidApi::class)
    fun onSendAnswersClick() {
        viewModelScope.launch {
            if (userAnswersCollector.state.value.userAnswers.isEmpty()) {
                _sessionState.update {
                    it.copy(
                        answersSendingState = AnswersSendingState.SUCCESS,
                        error = null,
                    )
                }

                return@launch
            }

            _sessionState.update {
                it.copy(
                    answersSendingState = AnswersSendingState.PENDING,
                )
            }
            timer.clear()

            val user = userDataStoreRepository.userFlow.first()
            val result = userAnswerRepository.insertAnswers(
                userUuid = user.userUuid,
                answers = userAnswersCollector.state.value.userAnswers,
            )

            _sessionState.update { state ->
                result.exceptionOrNull()?.let {
                    state.copy(
                        answersSendingState = AnswersSendingState.FAILURE,
                        error = it,
                    )
                } ?: state.copy(
                    answersSendingState = AnswersSendingState.SUCCESS,
                    error = null,
                )
            }
        }
    }

    companion object {
        private const val QUESTIONS_NUM = 5
    }

    private data class QuestionsSessionState(
        val questions: List<Question> = emptyList(),
        val questionNumber: Int = 0,
        val loadingState: LoadingState = LoadingState.IDLE,
        val answersSendingState: AnswersSendingState = AnswersSendingState.IDLE,
        val showExitDialog: Boolean = false,
        val error: Throwable? = null,
    ) {
        val hasOneQuestion get() = questions.size == 1
    }

    private enum class LoadingState {
        IDLE,
        PENDING,
        FAILURE,
    }

    private enum class AnswersSendingState {
        IDLE,
        PENDING,
        SUCCESS,
        FAILURE,
    }
}
