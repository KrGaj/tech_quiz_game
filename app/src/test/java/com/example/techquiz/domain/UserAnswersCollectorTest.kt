package com.example.techquiz.domain

import app.cash.turbine.test
import com.example.techquiz.domain.models.UserAnswer
import com.example.techquiz.testdata.Questions
import io.kotest.inspectors.forNone
import io.kotest.inspectors.forOne
import io.kotest.matchers.nulls.shouldNotBeNull
import io.kotest.matchers.shouldBe
import kotlinx.coroutines.test.runTest
import org.junit.Before
import org.junit.Test

class UserAnswersCollectorTest {
    private lateinit var collector: UserAnswersCollector

    @Before
    fun setUp() {
        collector = UserAnswersCollector()
    }

    @Test
    fun `Answers for question are collected`() = runTest {
        Questions.questions.forEach {
            collector.onOptionClick(
                option = it.options.random(),
                question = it,
            )
        }

        collector.state.test {
            awaitItem().userAnswers.map { it.question } shouldBe Questions.questions
        }
    }

    @Test
    fun `Selected options are added to the list`() = runTest {
        val question = Questions.questions.first()
        val options = listOf(question.options[0], question.options[2])

        options.forEach {
            collector.onOptionClick(
                option = it,
                question = question,
            )
        }

        collector.state.test {
            val state = awaitItem()
            val userAnswer = state.userAnswers.first()

            userAnswer.selectedOptions shouldBe options
        }
    }

    @Test
    fun `Already collected answer is modified`() = runTest {
        val questionDuplicate = Questions.questions.last().copy()

        Questions.questions.forEach {
            collector.onOptionClick(
                option = it.options.first(),
                question = it,
            )
        }

        collector.onOptionClick(
            option = questionDuplicate.options.last(),
            question = questionDuplicate,
        )

        collector.state.test {
            val state = awaitItem()
            state.userAnswers.size shouldBe Questions.questions.size

            state.userAnswers.forOne {
                it.question shouldBe questionDuplicate.copy()
            }

            val modifiedAnswer = state.userAnswers.find {
                it.question.text == questionDuplicate.text
            }
            modifiedAnswer shouldNotBeNull {
                selectedOptions shouldBe listOf(
                    questionDuplicate.options.first(),
                    questionDuplicate.options.last(),
                )
            }
        }
    }

    @Test
    fun `Selecting the same answer second time removes it from the list`() = runTest {
        val question = Questions.questions.last()
        val options = mutableListOf(
            question.options.first(),
            question.options.last(),
        )
        val optionToRemove = options.random()

        options.forEach {
            collector.onOptionClick(
                option = it,
                question = question,
            )
        }

        collector.onOptionClick(
            option = optionToRemove,
            question = question,
        )

        options.remove(optionToRemove)

        collector.state.test {
            val state = awaitItem()
            val userAnswer = state.userAnswers.first()

            userAnswer.selectedOptions shouldBe options
        }
    }

    @Test
    fun `Empty answer is added`() = runTest {
        collector.state.test {
            testScheduler.advanceUntilIdle()

            collector.addEmptyAnswer(
                question = Questions.questions.first(),
            )
            testScheduler.advanceUntilIdle()

            skipItems(1)
            awaitItem() shouldBe UserAnswersCollector.State(
                userAnswers = listOf(UserAnswer(question = Questions.questions.first())),
            )
        }
    }

    @Test
    fun `Empty answer to questions already having answers is not collected`() = runTest {
        collector.state.test {
            testScheduler.advanceUntilIdle()

            Questions.questions.slice(0..2).forEach {
                collector.onOptionClick(
                    option = it.options.random(),
                    question = it,
                )
                testScheduler.advanceUntilIdle()
            }

            collector.addEmptyAnswer(
                question = Questions.questions[2],
            )
            testScheduler.advanceUntilIdle()

            skipItems(3)

            val resultState = awaitItem()
            resultState.userAnswers.forOne {
                it.question shouldBe Questions.questions[2]
            }
            resultState.userAnswers.forNone {
                it.selectedOptions shouldBe emptyList()
            }
        }
    }
}
