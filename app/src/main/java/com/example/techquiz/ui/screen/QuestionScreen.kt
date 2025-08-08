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
import com.example.techquiz.ui.common.ShapedFilledTonalButton
import com.example.techquiz.ui.common.SpacedLazyVerticalGrid
import com.example.techquiz.ui.dialog.ExitDialog
import com.example.techquiz.ui.theme.CodingQuizTheme
import com.example.techquiz.util.getHttpFailureMessage
import com.example.techquiz.util.toggleValue
import com.example.techquiz.viewmodel.GivenAnswerViewModel
import com.example.techquiz.viewmodel.QuestionViewModel
import com.example.techquiz.viewmodel.TimerViewModel
import com.valentinilk.shimmer.shimmer
import kotlinx.coroutines.launch
import org.koin.androidx.compose.koinViewModel
import org.koin.core.parameter.parametersOf

private const val COLUMNS_NUM = 2

@Composable
fun QuestionScreen(
    questionViewModel: QuestionViewModel = koinViewModel(),
    givenAnswerViewModel: GivenAnswerViewModel = koinViewModel(),
    timerViewModel: TimerViewModel = koinViewModel { parametersOf(QuestionViewModel.TIMEOUT) },
    category: Category,
    navigateToCategories: () -> Unit,
    navigateToResults: (List<QuizResult>) -> Unit,
) {
    val coroutineScope = rememberCoroutineScope()
    val snackbarHostState = remember {
        SnackbarHostState()
    }
    val questionResult by questionViewModel.question
        .collectAsStateWithLifecycle()
    val answerAddResult by givenAnswerViewModel.answerAddResult
        .collectAsStateWithLifecycle()
    val questionNumber by questionViewModel.questionNumber
        .collectAsStateWithLifecycle()
    val selectedAnswers by givenAnswerViewModel.selectedAnswers
        .collectAsStateWithLifecycle()
    val timeLeft by timerViewModel.timeLeft
        .collectAsStateWithLifecycle()

    var question by remember {
        mutableStateOf(QuestionViewModel.DEFAULT_QUESTION)
    }

    var isLoading by remember {
        mutableStateOf(true)
    }

    val showExitDialog = rememberSaveable {
        mutableStateOf(false)
    }

    val context = LocalContext.current

    LaunchedEffect(questionResult) {
        questionResult?.fold(
            onSuccess = {
                isLoading = false
                question = it
                timerViewModel.start()
            },
            onFailure = {
                val messageRes = getHttpFailureMessage(it as? Exception)
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
                val messageRes = getHttpFailureMessage(it as? Exception)
                snackbarHostState.showSnackbar(context.getString(messageRes))
            }
        )
    }

    BackHandler {
        showExitDialog.toggleValue()
    }

    val dialogMessage = stringResource(id = R.string.quiz_exit_message)

    val onDialogDismiss: () -> Unit = {
        showExitDialog.toggleValue()
    }

    val onDialogConfirmation: () -> Unit = {
        showExitDialog.toggleValue()
        if (givenAnswerViewModel.quizResults.isEmpty()) {
            navigateToCategories()
        }
        else {
            coroutineScope.launch {
                timerViewModel.clear()
                givenAnswerViewModel.sendAnswers()
            }
        }
    }

    if (showExitDialog.value) {
        ExitDialog(
            message = dialogMessage,
            onDismissRequest = onDialogDismiss,
            onConfirmation = onDialogConfirmation,
        )
    }

    LaunchedEffect(Unit) {
        if (questionResult == null) {
            questionViewModel.fetchQuestions(category)
        }
    }

    val nextQuestionOrSendAnswers: () -> Unit = {
        timerViewModel.clear()
        givenAnswerViewModel.addAnswer(question)

        coroutineScope.launch {
            if (questionViewModel.isQuestionLast()) {
                givenAnswerViewModel.sendAnswers()
            } else {
                questionViewModel.nextQuestion()
            }
        }
        givenAnswerViewModel.clearSelectedAnswers()
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
                isLoading = isLoading,
                categoryName = category.name,
                questionNumber = questionNumber,
                multipleCorrectAnswers = question.answers.count { it.isCorrect } > 1,
            )
            QuestionTextCard(
                isLoading = isLoading,
                question = question,
            )
            AnswersGrid(
                isLoading = isLoading,
                answers = question.answers,
                selectedAnswers = selectedAnswers,
            ) {
                givenAnswerViewModel.toggleAnswer(it)
            }
            Timer(
                isLoading = isLoading,
                timeLeft = timeLeft,
            )
            NextQuestionButtonRow(
                isLoading = { isLoading },
                isQuestionLast = questionViewModel::isQuestionLast,
                onClick = nextQuestionOrSendAnswers,
            )
        }
    }

    if (timeLeft == 0L) {
        nextQuestionOrSendAnswers()
    }
}

@Composable
private fun QuestionHeader(
    isLoading: Boolean,
    categoryName: String,
    questionNumber: Int,
    multipleCorrectAnswers: Boolean,
) {
    if (isLoading) {
        QuestionHeaderLoading(
            categoryName = categoryName,
        )
    } else {
        QuestionHeaderLoaded(
            categoryName = categoryName,
            questionNumber = questionNumber,
            multipleCorrectAnswers = multipleCorrectAnswers,
        )
    }
}

@Composable
private fun QuestionHeaderLoading(
    categoryName: String,
) {
    HeaderTextLarge(
        modifier = Modifier
            .shimmer(),
        text = buildHeaderTextString(
            categoryName = categoryName,
            questionNumber = 1,
            multipleCorrectAnswers = false,
        ),
    )
}

@Composable
private fun QuestionHeaderLoaded(
    categoryName: String,
    questionNumber: Int,
    multipleCorrectAnswers: Boolean,
) {
    val headerText = buildHeaderTextString(
        categoryName,
        questionNumber,
        multipleCorrectAnswers,
    )

    HeaderTextLarge(
        text = headerText,
    )
}

@Composable
private fun buildHeaderTextString(
    categoryName: String,
    questionNumber: Int,
    multipleCorrectAnswers: Boolean
) = buildString {
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

@Composable
private fun QuestionTextCard(
    isLoading: Boolean,
    question: Question,
) {
    if (isLoading) {
        QuestionTextCardLoading()
    } else {
        QuestionTextCardLoaded(question = question)
    }
}

@Composable
private fun QuestionTextCardLoading() {
    TextCard(
        modifier = Modifier
            .shimmer(),
        text = "",
    )
}

@Composable
private fun QuestionTextCardLoaded(
    question: Question,
) {
    TextCard(text = question.text)
}

@Composable
private fun TextCard(
    modifier: Modifier = Modifier,
    textModifier: Modifier = Modifier,
    text: String,
) {
    Card(
        modifier = Modifier
            .padding(8.dp)
            .fillMaxWidth()
            .wrapContentHeight()
            .then(modifier),
    ) {
        Text(
            text = text,
            modifier = Modifier
                .padding(
                    horizontal = 24.dp,
                    vertical = 12.dp
                )
                .fillMaxWidth()
                .then(textModifier),
            textAlign = TextAlign.Center,
        )
    }
}

@Composable
private fun AnswersGrid(
    isLoading: Boolean,
    answers: List<PossibleAnswer>,
    selectedAnswers: List<PossibleAnswer>,
    onClick: (PossibleAnswer) -> Unit,
) {
    SpacedLazyVerticalGrid(
        columns = GridCells.Fixed(COLUMNS_NUM),
    ) {
        items(answers) {
            val isSelected = selectedAnswers.contains(it)

            val color =
                if (isSelected) MaterialTheme.colorScheme.primary
                else MaterialTheme.colorScheme.secondary

            PossibleAnswer(
                modifier = Modifier
                    .aspectRatio(1.5f),
                isLoading = isLoading,
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
    modifier: Modifier = Modifier,
    isLoading: Boolean,
    answer: PossibleAnswer,
    color: Color,
    onClick: () -> Unit,
) {
    if (isLoading) {
        PossibleAnswerLoading(modifier)
    } else {
        PossibleAnswerLoaded(
            modifier = modifier,
            answer = answer,
            color = color,
            onClick = onClick,
        )
    }
}

@Composable
private fun PossibleAnswerLoading(
    modifier: Modifier = Modifier,
) {
    ShapedFilledTonalButton(
        modifier = Modifier
            .then(modifier)
            .shimmer(),
        onClick = { },
    ) { }
}

@Composable
private fun PossibleAnswerLoaded(
    modifier: Modifier = Modifier,
    answer: PossibleAnswer,
    color: Color,
    onClick: () -> Unit,
) {
    ShapedFilledTonalButton(
        modifier = modifier,
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
private fun Timer(
    isLoading: Boolean,
    timeLeft: Long,
) {
    val timeLeftText = pluralStringResource(
        id = R.plurals.question_time_left,
        count = timeLeft.toInt(),
        timeLeft.toInt(),
    )

    var modifier = Modifier
        .fillMaxWidth()
        .padding(16.dp)

    if (isLoading) {
        modifier = modifier.shimmer()
    }

    Text(
        modifier = modifier,
        text = timeLeftText,
        textAlign = TextAlign.Center,
        fontSize = 24.sp,
    )
}

@Composable
private fun NextQuestionButtonRow(
    isLoading: () -> Boolean,
    isQuestionLast: () -> Boolean,
    onClick: () -> Unit,
) {
    if (isLoading()) {
        NextQuestionButtonRowLoading()
    } else {
        NextQuestionButtonRowLoaded(
            isQuestionLast = isQuestionLast,
            onClick = onClick,
        )
    }
}

@Composable
private fun NextQuestionButtonRowLoading() {
    Row(
        modifier = Modifier
            .fillMaxWidth(),
        horizontalArrangement = Arrangement.Center,
    ) {
        NextQuestionButton(
            modifier = Modifier
                .shimmer(),
            isQuestionLast = { false },
            onClick = { },
        )
    }
}

@Composable
private fun NextQuestionButtonRowLoaded(
    isQuestionLast: () -> Boolean,
    onClick: () -> Unit,
) {
    Row(
        modifier = Modifier
            .fillMaxWidth(),
        horizontalArrangement = Arrangement.Center,
    ) {
        NextQuestionButton(
            isQuestionLast = isQuestionLast,
            onClick = onClick,
        )
    }
}

@Composable
private fun NextQuestionButton(
    modifier: Modifier = Modifier,
    isQuestionLast: () -> Boolean,
    onClick: () -> Unit,
) {
    FilledTonalButton(
        modifier = modifier,
        onClick = onClick,
    ) {
        val textId =
            if (isQuestionLast()) R.string.question_finish
            else R.string.question_next

        Text(text = stringResource(id = textId))
    }
}


@Preview(showBackground = true)
@Composable
private fun PreviewQuestionHeaderLoading() {
    CodingQuizTheme {
        QuestionHeaderLoading(
            categoryName = question.category.name,
        )
    }
}

@Preview(showBackground = true)
@Composable
private fun PreviewQuestionHeaderLoaded() {
    CodingQuizTheme {
        QuestionHeaderLoaded(
            categoryName = question.category.name,
            questionNumber = questionNumber,
            multipleCorrectAnswers = true,
        )
    }
}

@Preview(showBackground = true)
@Composable
private fun PreviewQuestionTextCardLoading() {
    CodingQuizTheme {
        QuestionTextCardLoading()
    }
}

@Preview(showBackground = true)
@Composable
private fun PreviewQuestionTextCardLoaded() {
    CodingQuizTheme {
        QuestionTextCardLoaded(
            question = question,
        )
    }
}

@Preview(showBackground = true)
@Composable
private fun PreviewAnswersLoading() {
    CodingQuizTheme {
        AnswersGrid(
            isLoading = true,
            answers = answers,
            selectedAnswers = selectedAnswers,
        ) {}
    }
}

@Preview(showBackground = true)
@Composable
private fun PreviewAnswersLoaded() {
    CodingQuizTheme {
        AnswersGrid(
            isLoading = false,
            answers = answers,
            selectedAnswers = selectedAnswers,
        ) {}
    }
}

@Preview(showBackground = true)
@Composable
private fun PreviewTimerLoading() {
    CodingQuizTheme {
        Timer(
            isLoading = true,
            timeLeft = 2137,
        )
    }
}

@Preview(showBackground = true)
@Composable
private fun PreviewTimerLoaded() {
    CodingQuizTheme {
        Timer(
            isLoading = false,
            timeLeft = 2137
        )
    }
}

@Preview(showBackground = true)
@Composable
private fun PreviewNextQuestionButtonRowLoading() {
    CodingQuizTheme {
        NextQuestionButtonRowLoading()
    }
}

@Preview(showBackground = true)
@Composable
private fun PreviewNextQuestionButtonRowLoaded() {
    CodingQuizTheme {
        NextQuestionButtonRowLoaded({ false }) {}
    }
}

private val question = Question(
    id = 0,
    category = Category("Demo Category"),
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

private const val questionNumber = 3

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
