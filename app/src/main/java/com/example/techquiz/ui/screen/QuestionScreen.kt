package com.example.techquiz.ui.screen

import androidx.activity.compose.BackHandler
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.wrapContentHeight
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.FilledTonalButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.SnackbarHost
import androidx.compose.material3.SnackbarHostState
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.pluralStringResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.example.techquiz.R
import com.example.techquiz.data.domain.Category
import com.example.techquiz.data.domain.PossibleAnswer
import com.example.techquiz.data.domain.Question
import com.example.techquiz.data.domain.QuizResult
import com.example.techquiz.ui.common.HeaderTextLarge
import com.example.techquiz.ui.common.SpacedLazyVerticalGrid
import com.example.techquiz.ui.dialog.ExitDialog
import com.example.techquiz.ui.theme.CodingQuizTheme
import com.example.techquiz.util.getHttpFailureMessage
import com.example.techquiz.util.koinActivityViewModel
import com.example.techquiz.util.toggleValue
import com.example.techquiz.viewmodel.GivenAnswerViewModel
import com.example.techquiz.viewmodel.QuestionViewModel
import com.example.techquiz.viewmodel.TimerViewModel
import com.example.techquiz.viewmodel.UserViewModel
import io.ktor.client.plugins.ResponseException
import kotlinx.coroutines.launch
import org.koin.androidx.compose.koinViewModel
import org.koin.core.parameter.parametersOf

@Composable
fun QuestionScreen(
    questionViewModel: QuestionViewModel = koinViewModel(),
    givenAnswerViewModel: GivenAnswerViewModel = koinViewModel(),
    timerViewModel: TimerViewModel = koinViewModel { parametersOf(QuestionViewModel.TIMEOUT) },
    userViewModel: UserViewModel = koinActivityViewModel(),
    category: Category,
    navigateToCategories: () -> Unit,
    navigateToResults: (List<QuizResult>) -> Unit,
) {
    val coroutineScope = rememberCoroutineScope()
    val snackbarHostState = remember {
        SnackbarHostState()
    }
    val questionResult by questionViewModel.question.collectAsStateWithLifecycle()
    val answerAddResult by givenAnswerViewModel.answerAddResult.collectAsStateWithLifecycle()
    var question by remember {
        mutableStateOf(QuestionViewModel.DEFAULT_QUESTION)
    }
    val questionNumber by questionViewModel.questionNumber.collectAsStateWithLifecycle()
    val selectedAnswers by givenAnswerViewModel.selectedAnswers.collectAsStateWithLifecycle()
    val timeLeft by timerViewModel.timeLeft.collectAsStateWithLifecycle()

    val showExitDialog = rememberSaveable { mutableStateOf(false) }

    val context = LocalContext.current

    LaunchedEffect(questionResult) {
        givenAnswerViewModel.clearSelectedAnswers()
        questionResult.fold(
            onSuccess = {
                if (it != QuestionViewModel.DEFAULT_QUESTION) {
                    question = it
                    timerViewModel.start()
                }
            },
            onFailure = {
                val messageRes = getHttpFailureMessage(it as? ResponseException)
                snackbarHostState.showSnackbar(context.getString(messageRes))
            },
        )
    }

    LaunchedEffect(answerAddResult) {
        answerAddResult?.fold(
            onSuccess = {
                navigateToResults(givenAnswerViewModel.quizResults)
            },
            onFailure = {
                val messageRes = getHttpFailureMessage(it as? ResponseException)
                snackbarHostState.showSnackbar(context.getString(messageRes))
            }
        )
    }

    BackHandler {
        showExitDialog.toggleValue()
    }

    if (showExitDialog.value) {
        ExitDialog(
            message = stringResource(id = R.string.quiz_exit_message),
            onDismissRequest = { showExitDialog.toggleValue() },
            onConfirmation = {
                showExitDialog.toggleValue()
                if (givenAnswerViewModel.quizResults.isEmpty())
                    navigateToCategories()
                else coroutineScope.launch {
                    givenAnswerViewModel.sendAnswers(
                        userUUID = userViewModel.userUuid,
                        token = userViewModel.token,
                    )
                }
            },
        )
    }

    LaunchedEffect(Unit) {
        questionViewModel.fetchQuestions(category)
    }

    Scaffold(
        modifier = Modifier
            .padding(12.dp),
        snackbarHost = {
            SnackbarHost(hostState = snackbarHostState)
        },
    ) { paddingValues ->
        Column(
            modifier = Modifier
                .padding(paddingValues)
                .fillMaxSize(),
            verticalArrangement = Arrangement.SpaceBetween,
        ) {
            QuestionHeader(
                categoryName = category.name,
                questionNumber = questionNumber,
                multipleCorrectAnswers = question.answers.count { it.isCorrect } > 1,
            )
            QuestionTextCard(question)
            AnswersGrid(
                answers = question.answers,
                selectedAnswers = selectedAnswers,
            ) {
                givenAnswerViewModel.toggleAnswer(it)
            }
            Timer(timeLeft = timeLeft)
            NextQuestionButtonRow(
                isQuestionLast = questionViewModel::isQuestionLast,
            ) { isQuestionLast ->
                timerViewModel.clear()
                givenAnswerViewModel.addAnswer(question)

                if (isQuestionLast) {
                    coroutineScope.launch {
                        givenAnswerViewModel.sendAnswers(
                            userUUID = userViewModel.userUuid,
                            token = userViewModel.token,
                        )
                    }
                }

                questionViewModel.nextQuestion()
            }
        }
    }

    if (timeLeft == 0L) {
        givenAnswerViewModel.addAnswer(
            question = question,
        )
    }
}

@Composable
private fun QuestionHeader(
    categoryName: String,
    questionNumber: Int,
    multipleCorrectAnswers: Boolean,
) {
    val headerText = buildString {
        append(
            stringResource(
                id = R.string.question_header,
                categoryName,
                questionNumber,
            )
        )

        if (multipleCorrectAnswers) {
            append(" ")
            append(
                stringResource(
                    id = R.string.question_header_multiple_choice,
                )
            )
        }
    }

    HeaderTextLarge(
        text = headerText,
    )
}

@Composable
private fun QuestionTextCard(question: Question) {
    Card(
        modifier = Modifier
            .padding(8.dp)
            .fillMaxWidth()
            .wrapContentHeight(),
    ) {
        Text(
            text = question.text,
            modifier = Modifier
                .padding(
                    horizontal = 24.dp,
                    vertical = 12.dp
                )
                .fillMaxWidth(),
            textAlign = TextAlign.Center,
        )
    }
}

@Composable
private fun AnswersGrid(
    answers: List<PossibleAnswer>,
    selectedAnswers: List<PossibleAnswer>,
    onClick: (PossibleAnswer) -> Unit,
) {
    SpacedLazyVerticalGrid(
        columns = GridCells.Fixed(2),
    ) {
        items(answers) {
            val isSelected = selectedAnswers.contains(it)

            val color =
                if (isSelected) MaterialTheme.colorScheme.primary
                else MaterialTheme.colorScheme.secondary

            PossibleAnswer(
                answer = it,
                color,
            ) {
                onClick(it)
            }
        }
    }
}

@Composable
private fun PossibleAnswer(
    answer: PossibleAnswer,
    color: Color,
    onClick: () -> Unit,
) {
    FilledTonalButton(
        modifier = Modifier.aspectRatio(1.5f),
        shape = RoundedCornerShape(12.dp),
        colors = ButtonDefaults.buttonColors(color),
        onClick = onClick,
    ) {
        Text(
            text = answer.text,
            textAlign = TextAlign.Center,
        )
    }
}

@Composable
private fun Timer(timeLeft: Long) {
    val timeLeftText = pluralStringResource(
        id = R.plurals.question_time_left,
        count = timeLeft.toInt(),
        timeLeft.toInt(),
    )

    Text(
        modifier = Modifier
            .fillMaxWidth()
            .padding(16.dp),
        text = timeLeftText,
        textAlign = TextAlign.Center,
        fontSize = 24.sp,
    )
}

@Composable
private fun NextQuestionButtonRow(
    isQuestionLast: () -> Boolean,
    onClick: (Boolean) -> Unit,
) {
    Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.Center,
    ) {
        FilledTonalButton(
            onClick = {
                onClick(isQuestionLast())
            },
        ) {
            val textId =
                if (isQuestionLast()) R.string.question_finish
                else R.string.question_next

            Text(text = stringResource(id = textId))
        }
    }
}

@Preview(showBackground = true, apiLevel = 33)
@Composable
private fun PreviewQuestionTextCard() {
    CodingQuizTheme {
        QuestionTextCard(
            question = question,
        )
    }
}

@Preview(showBackground = true, apiLevel = 33)
@Composable
private fun PreviewAnswers() {
    CodingQuizTheme {
        AnswersGrid(
            answers = answers,
            selectedAnswers = selectedAnswers,
        ) {}
    }
}

@Preview(showBackground = true, apiLevel = 33)
@Composable
private fun PreviewTimer() {
    CodingQuizTheme {
        Timer(timeLeft = 2137)
    }
}

@Preview(showBackground = true, apiLevel = 33)
@Composable
private fun PreviewNextQuestionButtonRow() {
    CodingQuizTheme {
        NextQuestionButtonRow({ false }) {}
    }
}

private val question = Question(
    id = 0,
    category = Category("0"),
    text = "Lorem ipsum dolor sit amet, consectetur adipiscing elit, " +
            "sed do eiusmod tempor incididunt ut labore et dolore " +
            "magna aliqua. Ut enim ad minim veniam, quis nostrud " +
            "exercitation ullamco laboris nisi ut aliquip ex ea " +
            "commodo consequat. Duis aute irure dolor in " +
            "reprehenderit in voluptate velit esse cillum dolore " +
            "eu fugiat nulla pariatur. Excepteur sint occaecat " +
            "cupidatat non proident, sunt in culpa qui officia " +
            "deserunt mollit anim id est laborum.",
    answers = emptyList(),
)

private val answers = listOf(
    PossibleAnswer("Demo Answer 1", false),
    PossibleAnswer("Demo Answer 2", false),
    PossibleAnswer("Demo Answer 3", false),
    PossibleAnswer("Demo Answer 4", true),
)

private val selectedAnswers = listOf(
    PossibleAnswer("Demo Answer 1", false),
    PossibleAnswer("Demo Answer 4", true)
)
