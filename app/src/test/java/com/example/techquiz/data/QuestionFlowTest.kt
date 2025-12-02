package com.example.techquiz.data

import app.cash.turbine.test
import com.example.techquiz.data.domain.Category
import com.example.techquiz.data.domain.AnswerOption
import com.example.techquiz.data.domain.Question
import com.example.techquiz.data.repository.QuestionRepository
import io.kotest.matchers.shouldBe
import io.mockk.coEvery
import io.mockk.mockk
import kotlinx.coroutines.test.runTest
import org.junit.Before
import org.junit.Test

class QuestionFlowTest {
    private lateinit var questionFlow: QuestionFlow
    private lateinit var questionRepository: QuestionRepository

    @Before
    fun setUp() {
        questionRepository = mockk()
        questionFlow = QuestionFlow(questionRepository)
    }

    @Test
    fun `After loading questions, first question is exposed`() = runTest {
        coEvery {
            questionRepository.getRandomQuestions(any(), any())
        } returns Result.success(QUESTIONS)

        questionFlow.loadQuestions(CATEGORY, QUESTIONS.size)

        questionFlow.state.test {
            with(awaitItem()) {
                isCategoryEmpty shouldBe false
                currentQuestion shouldBe QUESTIONS.first()
                questionNumber shouldBe 1
                hasNextQuestion shouldBe true
                error shouldBe null
            }
        }
    }

    @Test
    fun `After nextQuestion() call, next question is exposed correctly`() = runTest {
        coEvery {
            questionRepository.getRandomQuestions(any(), any())
        } returns Result.success(QUESTIONS)

        questionFlow.loadQuestions(CATEGORY, QUESTIONS.size)
        questionFlow.nextQuestion()

        questionFlow.state.test {
            with(awaitItem()) {
                isCategoryEmpty shouldBe false
                currentQuestion shouldBe QUESTIONS[1]
                questionNumber shouldBe 2
                hasNextQuestion shouldBe true
                error shouldBe null
            }
        }
    }

    @Test
    fun `Last question is flagged correctly`() = runTest {
        coEvery {
            questionRepository.getRandomQuestions(any(), any())
        } returns Result.success(QUESTIONS)

        questionFlow.loadQuestions(CATEGORY, QUESTIONS.size)

        (QUESTIONS.size - 1 downTo 1).forEach { _ ->
            questionFlow.nextQuestion()
        }

        questionFlow.state.test {
            with(awaitItem()) {
                isCategoryEmpty shouldBe false
                currentQuestion shouldBe QUESTIONS.last()
                questionNumber shouldBe QUESTIONS.size
                hasNextQuestion shouldBe false
                error shouldBe null
            }
        }
    }

    @Test
    fun `Lack of questions for given category is flagged correctly`() = runTest {
        coEvery {
            questionRepository.getRandomQuestions(any(), any())
        } returns Result.success(emptyList())

        questionFlow.loadQuestions(CATEGORY, QUESTIONS.size)

        questionFlow.state.test {
            awaitItem() shouldBe QuestionFlow.State(isCategoryEmpty = true)
        }
    }

    @Test
    fun `Error from repository is exposed in state`() = runTest {
        val expectedException = IllegalStateException()

        coEvery {
            questionRepository.getRandomQuestions(any(), any())
        } returns Result.failure(expectedException)

        questionFlow.loadQuestions(CATEGORY, QUESTIONS.size)

        questionFlow.state.test {
            awaitItem() shouldBe QuestionFlow.State(error = expectedException)
        }
    }

    @Test
    fun `Nothing happens when trying to iterate outside question list`() = runTest {
        coEvery {
            questionRepository.getRandomQuestions(any(), any())
        } returns Result.success(QUESTIONS)

        questionFlow.loadQuestions(CATEGORY, QUESTIONS.size)

        (QUESTIONS.size - 1 downTo 1).forEach { _ ->
            questionFlow.nextQuestion()
        }

        questionFlow.nextQuestion()

        questionFlow.state.test {
            with(awaitItem()) {
                isCategoryEmpty shouldBe false
                currentQuestion shouldBe QUESTIONS.last()
                questionNumber shouldBe QUESTIONS.size
                hasNextQuestion shouldBe false
                error shouldBe null
            }
        }
    }

    companion object {
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