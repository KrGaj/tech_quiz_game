package com.example.techquiz.data

import app.cash.turbine.test
import com.example.techquiz.data.domain.AnswerOption
import com.example.techquiz.data.domain.Category
import com.example.techquiz.data.domain.Question
import io.kotest.inspectors.forOne
import io.kotest.matchers.nulls.shouldNotBeNull
import io.kotest.matchers.shouldBe
import kotlinx.coroutines.test.runTest
import org.junit.Before
import org.junit.Test

class SelectedAnswersCollectorTest {
    private lateinit var collector: UserAnswersCollector

    @Before
    fun setUp() {
        collector = UserAnswersCollector()
    }

    @Test
    fun `Answers for question are collected`() = runTest {
        QUESTIONS.forEach {
            collector.onOptionClick(
                option = it.options.random(),
                question = it,
            )
        }

        collector.state.test {
            awaitItem().userAnswers.map { it.question } shouldBe QUESTIONS
        }
    }

    @Test
    fun `Selected options are added to the list`() = runTest {
        val question = QUESTIONS.first()
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
        val questionDuplicate = QUESTIONS.last().copy()

        QUESTIONS.forEach {
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
            state.userAnswers.size shouldBe QUESTIONS.size

            state.userAnswers.forOne {
                it.question shouldBe questionDuplicate.copy()
            }

            val modifiedAnswer = state.userAnswers.find {
                it.question.id == questionDuplicate.id
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
        val question = QUESTIONS.last()
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
                        isCorrect = false,
                    ),
                    AnswerOption(
                        text = "No",
                        isCorrect = false,
                    ),
                    AnswerOption(
                        text = "Both",
                        isCorrect = false,
                    ),
                    AnswerOption(
                        text = "It depends",
                        isCorrect = true,
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
                    AnswerOption(
                        text = "C",
                        isCorrect = true,
                    ),
                    AnswerOption(
                        text = "D",
                        isCorrect = false,
                    ),
                ),
            ),
        )
    }
}
