package com.example.techquiz.data.remote.client

import com.example.techquiz.data.local.UserDataProvider
import com.example.techquiz.data.local.UserPreferences
import com.example.techquiz.data.remote.dto.answers_api.CategoryDTO
import com.example.techquiz.data.remote.dto.answers_api.QuestionDTO
import com.example.techquiz.data.remote.dto.answers_api.UserAnswerDTO
import com.example.techquiz.data.remote.dto.answers_api.UserDTO
import com.example.techquiz.data.remote.dto.answers_api.stats.CategoryStatsDTO
import com.example.techquiz.data.remote.dto.answers_api.stats.CorrectAnswersStatsDTO
import com.example.techquiz.util.TestDispatcherRule
import io.kotest.assertions.throwables.shouldThrow
import io.kotest.matchers.shouldBe
import io.ktor.client.plugins.ClientRequestException
import io.ktor.client.plugins.ServerResponseException
import io.ktor.http.HttpStatusCode
import io.mockk.every
import io.mockk.mockk
import kotlinx.coroutines.flow.flowOf
import kotlinx.coroutines.test.runTest
import org.junit.Before
import org.junit.Rule
import org.junit.Test
import java.util.Properties
import kotlin.random.Random
import kotlin.uuid.ExperimentalUuidApi
import kotlin.uuid.Uuid

class BackendApiClientTest {
    @get:Rule
    private val testDispatcherRule = TestDispatcherRule()

    private lateinit var clientProperties: Properties
    private lateinit var apiClient: BackendApiClient

    @Before
    fun setUp() {
        clientProperties = Properties().apply {
            set("serverHost", "abcde")
            set("serverPort", "12345")
        }
    }

    @OptIn(ExperimentalUuidApi::class)
    @Test
    fun `GIVEN user UUID and data WHEN save answers THEN return`() = runTest {
        val userUuid = Uuid.random()

        val testData = (1..5).map {
            UserAnswerDTO(
                userUuid = userUuid,
                question = QuestionDTO(
                    text = "Question $it",
                    category = CategoryDTO(name = "Category"),
                ),
                isCorrect = Random.nextBoolean(),
            )
        }

        val userDataProvider: UserDataProvider = mockk(relaxed = true) {
            val userPreferences = UserPreferences(
                userUuid = userUuid,
                userToken = "token",
            )
            every { userFlow } returns flowOf(userPreferences)
        }

        val apiClient = createApiClient(
            testData = testData,
            clientProperties = clientProperties,
            userDataProvider = userDataProvider,
        )

        val result = apiClient.addUserAnswers(
            answers = testData,
        )
        testScheduler.advanceUntilIdle()

        result shouldBe Unit
    }

    @OptIn(ExperimentalUuidApi::class)
    @Test
    fun `GIVEN user UUID and error WHEN send answers THEN throw`() = runTest {
        apiClient = createApiClientError(
            statusCode = HttpStatusCode.BadGateway,
        )

        shouldThrow<ServerResponseException> {
            apiClient.addUserAnswers(
                answers = mockk(relaxed = true),
            )
            testScheduler.advanceUntilIdle()
        }
    }

    @OptIn(ExperimentalUuidApi::class)
    @Test
    fun `GIVEN user UUID and categories count WHEN get most answered categories THEN return list of these`() =
        runTest {
            val expectedResult = listOf(
                CategoryStatsDTO(
                    category = CategoryDTO(
                        name = "Category 1",
                    ),
                    answersGiven = 10,
                ),
                CategoryStatsDTO(
                    category = CategoryDTO(
                        name = "Category 2",
                    ),
                    answersGiven = 15,
                ),
                CategoryStatsDTO(
                    category = CategoryDTO(
                        name = "Category 3",
                    ),
                    answersGiven = 7,
                ),
            )

            apiClient = createApiClient(
                testData = expectedResult,
                clientProperties = clientProperties,
            )

            val response = apiClient.getMostAnsweredCategories(
                userUuid = Uuid.random(),
                count = 3,
            )
            testScheduler.advanceUntilIdle()

            response shouldBe expectedResult
        }

    @OptIn(ExperimentalUuidApi::class)
    @Test
    fun `GIVEN user UUID and error WHEN get most answered categories THEN throw`() = runTest {
        apiClient = createApiClientError(
            statusCode = HttpStatusCode.Unauthorized,
        )

        shouldThrow<ClientRequestException> {
            apiClient.getMostAnsweredCategories(
                userUuid = Uuid.random(),
                count = 3,
            )
            testScheduler.advanceUntilIdle()
        }
    }

    @OptIn(ExperimentalUuidApi::class)
    @Test
    fun `GIVEN user UUID and correct answers count WHEN get correct answers count THEN return it`() =
        runTest {
            val expectedResult = CorrectAnswersStatsDTO(
                correctAnswers = 21,
                allAnswers = 37,
            )

            apiClient = createApiClient(
                testData = expectedResult,
                clientProperties = clientProperties,
            )

            val response = apiClient.getCorrectAnswersCount(
                userUuid = Uuid.random(),
            )
            testScheduler.advanceUntilIdle()

            response shouldBe expectedResult
        }

    @OptIn(ExperimentalUuidApi::class)
    @Test
    fun `GIVEN user UUID and error WHEN get correct answers count THEN throw`() = runTest {
        apiClient = createApiClientError(
            statusCode = HttpStatusCode.BadRequest,
        )

        shouldThrow<ClientRequestException> {
            apiClient.getCorrectAnswersCount(
                userUuid = Uuid.random(),
            )
            testScheduler.advanceUntilIdle()
        }
    }

    @OptIn(ExperimentalUuidApi::class)
    @Test
    fun `GIVEN user UUID WHEN get user THEN return it`() = runTest {
        val userUuid = Uuid.random()

        val userDataProvider: UserDataProvider = mockk(relaxed = true) {
            val userPreferences = UserPreferences(
                userUuid = userUuid,
                userToken = "token",
            )
            every { userFlow } returns flowOf(userPreferences)
        }

        val expectedResult = UserDTO(
            uuid = userUuid,
            username = "user",
            email = "email@domain.com",
        )

        apiClient = createApiClient(
            testData = expectedResult,
            clientProperties = clientProperties,
            userDataProvider = userDataProvider,
        )

        val response = apiClient.getUser()
        testScheduler.advanceUntilIdle()

        response shouldBe expectedResult
    }

    @OptIn(ExperimentalUuidApi::class)
    @Test
    fun `GIVEN error WHEN get user THEN throw`() = runTest {
        apiClient = createApiClientError(
            statusCode = HttpStatusCode.InternalServerError,
        )

        shouldThrow<ServerResponseException> {
            apiClient.getUser()
            testScheduler.advanceUntilIdle()
        }
    }

    private inline fun <reified T> createApiClient(
        testData: T,
        clientProperties: Properties,
        userDataProvider: UserDataProvider = mockk(relaxed = true),
    ) = BackendApiClient(
        engine = createMockEngine(testData),
        clientProperties = clientProperties,
        userDataProvider = userDataProvider,
    )

    private fun createApiClientError(
        statusCode: HttpStatusCode,
        userDataProvider: UserDataProvider = mockk(relaxed = true),
    ) = BackendApiClient(
        engine = createMockEngineError(statusCode),
        clientProperties = clientProperties,
        userDataProvider = userDataProvider,
    )
}
