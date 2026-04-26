package com.example.techquiz.app.ui.question

import app.cash.turbine.test
import com.example.techquiz.app.ui.mapper.toQuestionDataUiState
import com.example.techquiz.data.local.UserPreferences
import com.example.techquiz.data.local.UserDataProvider
import com.example.techquiz.domain.Timer
import com.example.techquiz.domain.UserAnswersCollector
import com.example.techquiz.domain.models.UserAnswer
import com.example.techquiz.domain.repository.QuestionRepository
import com.example.techquiz.domain.repository.UserAnswerRepository
import com.example.techquiz.testdata.Questions
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
import io.mockk.verify
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
    private lateinit var userDataProvider: UserDataProvider
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
        userDataProvider = mockk()
        userAnswersCollector = mockk()
        timer = mockk()

        userAnswers = Questions.questions.map {
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
            userAnswersCollector.addEmptyAnswer(any())
        } returns Unit

        every {
            userAnswersCollector.onOptionClick(any(), any())
        } returns Unit

        every {
            timer.start(any(), any())
        } returns Unit

        every {
            timer.clear()
        } returns Unit

        coEvery {
            timer.timeLeft
        } returns MutableStateFlow(Questions.timeout).asStateFlow()

        coEvery {
            questionRepository.getRandomQuestions(any(), any())
        } coAnswers {
            delay(1.seconds)
            Result.success(Questions.questions.toList())
        }

        coEvery {
            userAnswerRepository.insertAnswers(any(), any())
        } coAnswers {
            delay(1.seconds)
            Result.success(Unit)
        }

        coEvery {
            userDataProvider.userFlow
        } returns flowOf(UserPreferences(
            userUuid = Uuid.random(),
            userToken = "Demo token",
        ))

        viewModel = QuestionViewModel(
            category = Questions.category,
            questionRepository = questionRepository,
            userAnswerRepository = userAnswerRepository,
            userDataProvider = userDataProvider,
            userAnswersCollector = userAnswersCollector,
            timer = timer,
            timeout = Questions.timeout,
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
                question = Questions.questions.first().toQuestionDataUiState(
                    questionNumber = 1,
                    selectedOptions = userAnswers.first().selectedOptions,
                    isLast = false,
                ),
                timeLeft = Questions.timeout.inWholeSeconds,
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
                it.question.questionText shouldBe Questions.questions[1].text
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
                it.question shouldBe Questions.questions[1].toQuestionDataUiState(
                    questionNumber = 2,
                    selectedOptions = userAnswers[1].selectedOptions,
                    isLast = false,
                )
                it.timeLeft shouldBe Questions.timeout.inWholeSeconds
            }
        }
    }

    @Test
    fun `Last question is flagged correctly`() = runTest {
        viewModel.uiState.test {
            testScheduler.advanceUntilIdle()

            (0..<Questions.questions.size - 1).forEach { _ ->
                viewModel.onNextQuestionClick()
                testScheduler.advanceUntilIdle()
            }

            skipItems(5)

            awaitItem() shouldBe QuestionUiState.Success(
                question = Questions.questions.last().toQuestionDataUiState(
                    questionNumber = Questions.questions.size,
                    selectedOptions = userAnswers.last().selectedOptions,
                    isLast = true,
                ),
                timeLeft = Questions.timeout.inWholeSeconds,
            )
        }
    }

    @Test
    fun `Trying to load question next to the last one does nothing`() = runTest {
        viewModel.uiState.test {
            testScheduler.advanceUntilIdle()

            (0..<Questions.questions.size - 1).forEach { _ ->
                viewModel.onNextQuestionClick()
                testScheduler.advanceUntilIdle()
            }

            skipItems(6)

            viewModel.onNextQuestionClick()
            testScheduler.advanceUntilIdle()

            shouldNotThrow<AssertionError> { expectNoEvents() }
        }
    }

    @Test
    fun `Timeout causes move to the next question`() = runTest {
        val onTimeoutSlot = slot<suspend () -> Unit>()

        every {
            timer.start(any(), capture(onTimeoutSlot))
        } returns Unit

        viewModel.uiState.test {
            testScheduler.advanceUntilIdle()

            onTimeoutSlot.captured.invoke()
            testScheduler.advanceUntilIdle()

            skipItems(2)
            awaitItem() shouldBe QuestionUiState.Success(
                question = Questions.questions[1].toQuestionDataUiState(
                    questionNumber = 2,
                    selectedOptions = userAnswers[1].selectedOptions,
                    isLast = false,
                ),
                timeLeft = Questions.timeout.inWholeSeconds,
            )
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

            Questions.questions.forEach { _ ->
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

    @OptIn(ExperimentalUuidApi::class)
    @Test
    fun `After timeout, answers are collected`() = runTest {
        val onTimeoutSlot = slot<suspend () -> Unit>()

        every {
            timer.start(any(), capture(onTimeoutSlot))
        } returns Unit

        viewModel.uiState.test {
            testScheduler.advanceUntilIdle()

            onTimeoutSlot.captured.invoke()
            testScheduler.advanceUntilIdle()

            userAnswers[1].selectedOptions.forEach {
                viewModel.onAnswerOptionClick(
                    option = it,
                )
            }

            onTimeoutSlot.captured.invoke()
            testScheduler.advanceUntilIdle()

            verify(exactly = 2) { userAnswersCollector.addEmptyAnswer(any()) }

            cancelAndIgnoreRemainingEvents()
        }
    }

    @Test
    fun `Sending answers is indicated correctly`() = runTest {
        viewModel.uiState.test {
            testScheduler.advanceUntilIdle()

            Questions.questions.forEach { _ ->
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

            (0..<Questions.questions.size - 1).forEach { _ ->
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
}
