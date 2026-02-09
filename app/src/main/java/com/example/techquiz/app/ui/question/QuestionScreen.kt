package com.example.techquiz.app.ui.question

import androidx.activity.compose.BackHandler
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
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
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.pluralStringResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.example.techquiz.R
import com.example.techquiz.app.ui.common.ErrorScreen
import com.example.techquiz.app.ui.common.LoadingScreen
import com.example.techquiz.app.ui.mapper.toQuestionDataUiState
import com.example.techquiz.domain.models.AnswerOption
import com.example.techquiz.data.domain.Category
import com.example.techquiz.domain.models.Question
import com.example.techquiz.domain.models.UserAnswer
import com.example.techquiz.ui.common.HeaderTextLarge
import com.example.techquiz.ui.common.ShapedFilledTonalButton
import com.example.techquiz.ui.common.SpacedLazyVerticalGrid
import com.example.techquiz.ui.theme.CodingQuizTheme
import com.example.techquiz.ui.theme.Typography

private const val COLUMNS_NUM = 2

// TODO refactor modifiers

@Composable
fun QuestionScreen(
    questionViewModel: QuestionViewModel,
    navigateFromQuestion: (List<UserAnswer>) -> Unit,
) {
    val uiState by questionViewModel.uiState.collectAsStateWithLifecycle()

    BackHandler {
        questionViewModel.setExitDialogVisibility(value = true)
    }

    QuestionScreen(
        uiState = uiState,
        onDialogConfirm = questionViewModel::onSendAnswersClick,
        onDialogDismiss = { questionViewModel.setExitDialogVisibility(false) },
        onAnswerOptionClick = {
            questionViewModel.onAnswerOptionClick(
                option = it,
            )
        },
        onNextQuestionClick = questionViewModel::onNextQuestionClick,
        onSendAnswersClick = questionViewModel::onSendAnswersClick,
        onRetryClick = questionViewModel::onRetryClick,
        navigateFromQuestion = navigateFromQuestion,
    )
}

@Composable
private fun QuestionScreen(
    uiState: QuestionUiState,
    onDialogConfirm: () -> Unit,
    onDialogDismiss: () -> Unit,
    onAnswerOptionClick: (AnswerOption) -> Unit,
    onNextQuestionClick: () -> Unit,
    onSendAnswersClick: () -> Unit,
    onRetryClick: () -> Unit,
    navigateFromQuestion: (List<UserAnswer>) -> Unit,
) {
    when(uiState) {
        is QuestionUiState.Success if uiState.isExitDialogVisible -> ExitDialog(
            message = stringResource(id = R.string.quiz_exit_message),
            onConfirm = onDialogConfirm,
            onDismiss = onDialogDismiss,
        )
        is QuestionUiState.Success -> QuestionScreenSuccess(
            uiState = uiState,
            onAnswerOptionClick = { onAnswerOptionClick(it.option) },
            onNextQuestionClick = onNextQuestionClick,
            onSendAnswersClick = onSendAnswersClick,
        )
        is QuestionUiState.Loading -> LoadingScreen()
        is QuestionUiState.EmptyCategory -> EmptyCategoryQuestionScreen()
        is QuestionUiState.AnswersSent -> navigateFromQuestion(uiState.userAnswers)
        is QuestionUiState.Error -> ErrorScreen(
            errorMessage = stringResource(id = uiState.errorMsgRes),
            onRetryClick = onRetryClick,
        )
    }
}

@Composable
private fun QuestionScreenSuccess(
    uiState: QuestionUiState.Success,
    onAnswerOptionClick: (AnswerOptionUiState) -> Unit,
    onNextQuestionClick: () -> Unit,
    onSendAnswersClick: () -> Unit,
) {
    Column(
        modifier = Modifier
            .fillMaxSize(),
        verticalArrangement = Arrangement.SpaceBetween,
    ) {
        QuestionHeaderLoaded(
            categoryName = uiState.question.categoryName,
            questionNumber = uiState.question.questionNumber,
            multipleCorrectAnswers = uiState.question.multipleCorrectAnswers,
        )
        QuestionTextCardLoaded(
            question = uiState.question,
        )
        AnswerOptionsGridLoaded(
            answers = uiState.question.options,
            onClick = {
                onAnswerOptionClick(it)
            },
        )
        TimerLoaded(
            timeLeft = uiState.timeLeft,
        )
        BottomButtonRowLoaded(
            isQuestionLast = { uiState.question.isLast },
            onNextQuestionClick = onNextQuestionClick,
            onSendAnswersClick = onSendAnswersClick,
        )
    }
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
private fun QuestionTextCardLoaded(
    question: QuestionDataUiState,
) {
    TextCard(text = question.questionText)
}

@Composable
private fun AnswerOptionsGridLoaded(
    answers: List<AnswerOptionUiState>,
    onClick: (AnswerOptionUiState) -> Unit,
) {
    SpacedLazyVerticalGrid(
        columns = GridCells.Fixed(COLUMNS_NUM),
    ) {
        items(answers) {
            val color =
                if (it.isSelected) MaterialTheme.colorScheme.primary
                else MaterialTheme.colorScheme.secondary

            AnswerOptionLoaded(
                modifier = Modifier
                    .aspectRatio(1.5f),
                answer = it,
                color,
            ) {
                onClick(it)
            }
        }
    }
}

@Composable
private fun AnswerOptionLoaded(
    modifier: Modifier = Modifier,
    answer: AnswerOptionUiState,
    color: Color,
    onClick: () -> Unit,
) {
    ShapedFilledTonalButton(
        modifier = modifier,
        colors = ButtonDefaults.buttonColors(color),
        onClick = onClick,
    ) {
        Text(
            text = answer.option.text,
            textAlign = TextAlign.Center,
        )
    }
}

@Composable
private fun TimerLoaded(
    timeLeft: Long,
) {
    Timer(
        timeLeft = timeLeft,
    )
}

@Composable
private fun BottomButtonRowLoaded(
    isQuestionLast: () -> Boolean,
    onNextQuestionClick: () -> Unit,
    onSendAnswersClick: () -> Unit,
) {
    Row(
        modifier = Modifier
            .fillMaxWidth(),
        horizontalArrangement = Arrangement.Center,
    ) {
        if (isQuestionLast()) {
            SendAnswersButton(
                onClick = onSendAnswersClick,
            )
        } else {
            NextQuestionButton(
                onClick = onNextQuestionClick,
            )
        }
    }
}

@Composable
private fun SendAnswersButton(
    modifier: Modifier = Modifier,
    onClick: () -> Unit,
) {
    FilledTonalButton(
        modifier = modifier,
        onClick = onClick,
    ) {
        Text(text = stringResource(id = R.string.question_finish))
    }
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
private fun Timer(
    timeLeft: Long,
) {
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
private fun NextQuestionButton(
    modifier: Modifier = Modifier,
    onClick: () -> Unit,
) {
    FilledTonalButton(
        modifier = modifier,
        onClick = onClick,
    ) {
        Text(text = stringResource(id = R.string.question_next))
    }
}

@Composable
private fun EmptyCategoryQuestionScreen() {
    Box(
        modifier = Modifier.fillMaxSize(),
        contentAlignment = Alignment.Center,
    ) {
        Text(
            text = stringResource(id = R.string.question_empty_category),
            style = Typography.bodyLarge,
        )
    }
}


@Preview(showBackground = true)
@Composable
private fun PreviewQuestionScreenSuccess() {
    CodingQuizTheme {
        QuestionScreenSuccess(
            uiState = UI_STATE_SUCCESS,
            onAnswerOptionClick = {},
            onNextQuestionClick = {},
            onSendAnswersClick = {},
        )
    }
}

private const val QUESTION_NUMBER = 3

private val ANSWER_OPTIONS = listOf(
    AnswerOption("Demo Answer 1", false),
    AnswerOption("Demo Answer 2", false),
    AnswerOption("Demo Answer 3", false),
    AnswerOption("Demo Answer 4", true),
)

private val SELECTED_OPTIONS = listOf(
    AnswerOption("Demo Answer 1", false),
    AnswerOption("Demo Answer 4", true)
)

private val QUESTION = Question(
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
    options = ANSWER_OPTIONS,
)

private val UI_STATE_SUCCESS = QuestionUiState.Success(
    question = QUESTION.toQuestionDataUiState(
        questionNumber = QUESTION_NUMBER,
        selectedOptions = SELECTED_OPTIONS,
        isLast = false,
    ),
    timeLeft = 30,
    isExitDialogVisible = false,
)
