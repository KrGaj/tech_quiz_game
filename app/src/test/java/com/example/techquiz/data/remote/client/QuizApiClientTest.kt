package com.example.techquiz.data.remote.client

import com.example.techquiz.data.remote.dto.quiz_api.CategoriesDTO
import com.example.techquiz.data.remote.dto.quiz_api.CategoryDTO
import com.example.techquiz.data.remote.dto.quiz_api.QuestionDTO
import com.example.techquiz.data.remote.dto.quiz_api.QuestionsDTO
import com.example.techquiz.util.TestDispatcherRule
import io.kotest.assertions.throwables.shouldThrow
import io.kotest.matchers.shouldBe
import io.ktor.client.plugins.ClientRequestException
import io.ktor.client.plugins.ServerResponseException
import io.ktor.http.HttpStatusCode
import kotlinx.coroutines.test.runTest
import org.junit.Rule
import org.junit.Test

class QuizApiClientTest {
    @get:Rule
    private val testDispatcherRule = TestDispatcherRule()

    private lateinit var apiClient: QuizApiClient

    @Test
    fun `WHEN call categories endpoint THEN list of categories is returned`() = runTest {
        val expectedResult = CategoriesDTO(
            content = listOf(
                CategoryDTO(
                    id = 0,
                    name = "Category 1",
                ),
                CategoryDTO(
                    id = 0,
                    name = "Category 2",
                ),
            ),
        )

        apiClient = createApiClient(expectedResult)

        val result = apiClient.getCategories()
        testScheduler.advanceUntilIdle()

        result shouldBe expectedResult
    }

    @Test
    fun `WHEN call categories endpoint THEN exception is thrown`() = runTest {
        apiClient = createApiClientError(statusCode = HttpStatusCode.NotFound)

        shouldThrow<ClientRequestException> {
            apiClient.getCategories()
            testScheduler.advanceUntilIdle()
        }
    }

    @Test
    fun `GIVEN category ID WHEN call questions endpoint THEN questions are returned`() = runTest {
        val expectedResult = QuestionsDTO(
            content = listOf(
                QuestionDTO(
                    questionText = "question 1",
                    category = "category 1",
                    correctAnswer = "yes",
                    incorrectAnswers = listOf("no"),
                ),
                QuestionDTO(
                    questionText = "question 2",
                    category = "category 1",
                    correctAnswer = "maybe",
                    incorrectAnswers = listOf("or not maybe"),
                ),
            ),
        )

        apiClient = createApiClient(
            testData = expectedResult,
        )

        val result = apiClient.getQuestions(
            category = 1,
            amount = 2,
        )
        testScheduler.advanceUntilIdle()

        result shouldBe expectedResult
    }

    @Test
    fun `GIVEN category ID WHEN call questions endpoint THEN error is thrown`() = runTest {
        apiClient = createApiClientError(
            statusCode = HttpStatusCode.InternalServerError,
        )

        shouldThrow<ServerResponseException> {
            apiClient.getQuestions(
                category = 1,
                amount = 2,
            )
            testScheduler.advanceUntilIdle()
        }
    }

    private inline fun <reified T> createApiClient(
        testData: T,
    ) = QuizApiClient(
        engine = createMockEngine(testData),
    )

    private fun createApiClientError(
        statusCode: HttpStatusCode,
    ) = QuizApiClient(
        engine = createMockEngineError(statusCode),
    )
}
