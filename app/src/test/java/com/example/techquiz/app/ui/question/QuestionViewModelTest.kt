package com.example.techquiz.app.ui.question

import app.cash.turbine.test
import com.example.techquiz.app.ui.mapper.toQuestionDataUiState
import com.example.techquiz.domain.Timer
import com.example.techquiz.domain.UserAnswersCollector
import com.example.techquiz.domain.models.AnswerOption
import com.example.techquiz.data.domain.Category
import com.example.techquiz.domain.models.Question
import com.example.techquiz.domain.models.UserAnswer
import com.example.techquiz.data.domain.UserPreferences
import com.example.techquiz.domain.repository.QuestionRepository
import com.example.techquiz.domain.repository.UserAnswerRepository
import com.example.techquiz.data.repository.UserDataStoreRepository
import com.example.techquiz.util.getHttpFailureMessage
import io.kotest.assertions.throwables.shouldNotThrow
import io.kotest.matchers.shouldBe
import io.kotest.matchers.shouldNotBe
import io.kotest.matchers.types.shouldBeTypeOf
import io.mockk.coEvery
import io.mockk.coVerify
import io.mockk.every
import io.mockk.mockk
import io.mockk.slot
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.flowOf
import kotlinx.coroutines.test.StandardTestDispatcher
import kotlinx.coroutines.test.resetMain
import kotlinx.coroutines.test.runTest
import kotlinx.coroutines.test.setMain
import org.junit.After
import org.junit.Before
import org.junit.Test
import kotlin.time.Duration.Companion.seconds
import kotlin.uuid.ExperimentalUuidApi
import kotlin.uuid.Uuid

class QuestionViewModelTest {
    private lateinit var viewModel: QuestionViewModel

    private lateinit var questionRepository: QuestionRepository
    private lateinit var userAnswerRepository: UserAnswerRepository
    private lateinit var userDataStoreRepository: UserDataStoreRepository
    private lateinit var userAnswersCollector: UserAnswersCollector
    private lateinit var timer: Timer

    private lateinit var userAnswers: List<UserAnswer>

    @OptIn(
        ExperimentalCoroutinesApi::class,
        ExperimentalUuidApi::class,
    )
    @Before
    fun setUp() {
        Dispatchers.setMain(StandardTestDispatcher())

        questionRepository = mockk()
        userAnswerRepository = mockk()
        userDataStoreRepository = mockk()
        userAnswersCollector = mockk()
        timer = mockk()

        userAnswers = QUESTIONS.map {
            UserAnswer(
                question = it,
                selectedOptions = listOf(it.options.random()),
            )
        }

        coEvery {
            userAnswersCollector.state
        } returns MutableStateFlow(UserAnswersCollector.State(userAnswers))
            .asStateFlow()

        every {
            timer.start(any(), any())
        } returns Unit

        every {
            timer.clear()
        } returns Unit

        coEvery {
            timer.timeLeft
        } returns MutableStateFlow(TIMEOUT).asStateFlow()

        coEvery {
            questionRepository.getRandomQuestions(any(), any())
        } coAnswers {
            delay(1.seconds)
            Result.success(QUESTIONS.toList())
        }

        coEvery {
            userAnswerRepository.insertAnswers(any(), any())
        } coAnswers {
            delay(1.seconds)
            Result.success(Unit)
        }

        coEvery {
            userDataStoreRepository.userFlow
        } returns flowOf(UserPreferences(
            userUuid = Uuid.random(),
            userToken = "Demo token",
        ))

        viewModel = QuestionViewModel(
            category = CATEGORY,
            questionRepository = questionRepository,
            userAnswerRepository = userAnswerRepository,
            userDataStoreRepository = userDataStoreRepository,
            userAnswersCollector = userAnswersCollector,
            timer = timer,
            timeout = TIMEOUT,
        )
    }

    @OptIn(ExperimentalCoroutinesApi::class)
    @After
    fun tearDown() {
        Dispatchers.resetMain()
    }

    @Test
    fun `Fetching data updates UI state`() = runTest {
        viewModel.uiState.test {
            testScheduler.advanceUntilIdle()
            skipItems(1)

            awaitItem() shouldBe QuestionUiState.Success(
                question = QUESTIONS.first().toQuestionDataUiState(
                    questionNumber = 1,
                    selectedOptions = userAnswers.first().selectedOptions,
                    isLast = false,
                ),
                timeLeft = TIMEOUT.inWholeSeconds,
            )
        }
    }

    @Test
    fun `Error is exposed to the state`() = runTest {
        coEvery {
            questionRepository.getRandomQuestions(any(), any())
        } coAnswers {
            delay(1.seconds)
            Result.failure(IllegalStateException())
        }

        viewModel.uiState.test {
            testScheduler.advanceUntilIdle()
            skipItems(1)

            awaitItem() shouldBe QuestionUiState.Error(
                getHttpFailureMessage(IllegalStateException()),
            )
        }
    }

    @Test
    fun `Loading is indicated correctly`() = runTest {
        viewModel.uiState.test {
            testScheduler.advanceUntilIdle()

            val initialState = awaitItem()
            initialState shouldBe QuestionUiState.Loading

            val finalState = awaitItem()
            finalState shouldNotBe QuestionUiState.Loading
        }
    }

    @Test
    fun `On timeout, next question is loaded`() = runTest {
        val onTimeoutSlot = slot<suspend () -> Unit>()

        every {
            timer.start(any(), capture(onTimeoutSlot))
        } returns Unit

        viewModel.uiState.test {
            testScheduler.advanceUntilIdle()
            skipItems(2)

            onTimeoutSlot.captured.invoke()

            awaitItem().let {
                it.shouldBeTypeOf<QuestionUiState.Success>()
                it.question.questionText shouldBe QUESTIONS[1].text
            }

        }
    }

    @Test
    fun `Next question is exposed correctly`() = runTest {
        viewModel.uiState.test {
            testScheduler.advanceUntilIdle()
            viewModel.onNextQuestionClick()

            skipItems(2)
            awaitItem().let {
                it.shouldBeTypeOf<QuestionUiState.Success>()
                it.question shouldBe QUESTIONS[1].toQuestionDataUiState(
                    questionNumber = 2,
                    selectedOptions = userAnswers[1].selectedOptions,
                    isLast = false,
                )
                it.timeLeft shouldBe TIMEOUT.inWholeSeconds
            }
        }
    }

    @Test
    fun `Last question is flagged correctly`() = runTest {
        viewModel.uiState.test {
            testScheduler.advanceUntilIdle()

            (0..<QUESTIONS.size - 1).forEach { _ ->
                viewModel.onNextQuestionClick()
                testScheduler.advanceUntilIdle()
            }

            skipItems(5)

            awaitItem() shouldBe QuestionUiState.Success(
                question = QUESTIONS.last().toQuestionDataUiState(
                    questionNumber = QUESTIONS.size,
                    selectedOptions = userAnswers.last().selectedOptions,
                    isLast = true,
                ),
                timeLeft = TIMEOUT.inWholeSeconds,
            )
        }
    }

    @Test
    fun `Trying to load question next to the last one does nothing`() = runTest {
        viewModel.uiState.test {
            testScheduler.advanceUntilIdle()

            (0..<QUESTIONS.size - 1).forEach { _ ->
                viewModel.onNextQuestionClick()
                testScheduler.advanceUntilIdle()
            }

            skipItems(6)

            viewModel.onNextQuestionClick()
            testScheduler.advanceUntilIdle()

            shouldNotThrow<AssertionError> { expectNoEvents() }
        }
    }

    @OptIn(ExperimentalUuidApi::class)
    @Test
    fun `Timeout on last question causes sending all answers`() = runTest {
        val onTimeoutSlot = slot<suspend () -> Unit>()

        every {
            timer.start(any(), capture(onTimeoutSlot))
        } returns Unit

        viewModel.uiState.test {
            testScheduler.advanceUntilIdle()

            QUESTIONS.forEach { _ ->
                viewModel.onNextQuestionClick()
                testScheduler.advanceUntilIdle()
            }

            skipItems(6)
            onTimeoutSlot.captured.invoke()
            testScheduler.advanceUntilIdle()

            coVerify(exactly = 1) { userAnswerRepository.insertAnswers(any(), any()) }

            cancelAndIgnoreRemainingEvents()
        }
    }

    @Test
    fun `Sending answers is indicated correctly`() = runTest {
        viewModel.uiState.test {
            testScheduler.advanceUntilIdle()

            QUESTIONS.forEach { _ ->
                viewModel.onNextQuestionClick()
                testScheduler.advanceUntilIdle()
            }

            skipItems(6)

            viewModel.onSendAnswersClick()
            testScheduler.advanceUntilIdle()

            awaitItem() shouldBe QuestionUiState.Loading
            awaitItem() shouldBe QuestionUiState.AnswersSent(userAnswers)
        }
    }

    @OptIn(ExperimentalUuidApi::class)
    @Test
    fun `Sending answers failure is indicated`() = runTest {
        coEvery {
            userAnswerRepository.insertAnswers(any(), any())
        } coAnswers {
            delay(1.seconds)
            Result.failure(IllegalStateException())
        }

        viewModel.uiState.test {
            testScheduler.advanceUntilIdle()

            (0..<QUESTIONS.size - 1).forEach { _ ->
                viewModel.onNextQuestionClick()
                testScheduler.advanceUntilIdle()
            }

            viewModel.onSendAnswersClick()
            testScheduler.advanceUntilIdle()

            skipItems(7)

            awaitItem() shouldBe QuestionUiState.Error(
                getHttpFailureMessage(IllegalStateException()),
            )
        }
    }


    companion object {
        private val TIMEOUT = 5.seconds

        private val CATEGORY = Category(name = "Demo Category")

        private val QUESTIONS = listOf(
            Question(
                id = 1,
                category = CATEGORY,
                text = "Question ABC",
                options = listOf(
                    AnswerOption(
                        text = "Yes",
                        isCorrect = true,
                    ),
                    AnswerOption(
                        text = "No",
                        isCorrect = false,
                    ),
                ),
            ),
            Question(
                id = 2,
                category = CATEGORY,
                text = "Question xD",
                options = listOf(
                    AnswerOption(
                        text = "A",
                        isCorrect = false,
                    ),
                    AnswerOption(
                        text = "B",
                        isCorrect = true,
                    ),
                ),
            ),
            Question(
                id = 3,
                category = CATEGORY,
                text = "Example Question",
                options = listOf(
                    AnswerOption(
                        text = "1",
                        isCorrect = true,
                    ),
                    AnswerOption(
                        text = "2",
                        isCorrect = false,
                    ),
                ),
            ),
            Question(
                id = 5,
                category = CATEGORY,
                text = "Question DEF",
                options = listOf(
                    AnswerOption(
                        text = "Up",
                        isCorrect = true,
                    ),
                    AnswerOption(
                        text = "Down",
                        isCorrect = false,
                    ),
                ),
            ),
            Question(
                id = 8,
                category = CATEGORY,
                text = "Question MZ ETZ 251",
                options = listOf(
                    AnswerOption(
                        text = "Slow",
                        isCorrect = false,
                    ),
                    AnswerOption(
                        text = "Fast",
                        isCorrect = true,
                    ),
                ),
            ),
        )
    }
}