package com.example.techquiz.ui.screen

import android.util.Log
import androidx.activity.compose.BackHandler
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.wrapContentHeight
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.FilledTonalButton
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
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
import com.example.techquiz.data.domain.Category
import com.example.techquiz.data.domain.GivenAnswer
import com.example.techquiz.data.domain.PossibleAnswer
import com.example.techquiz.data.domain.Question
import com.example.techquiz.data.domain.QuizResult
import com.example.techquiz.ui.common.HeaderTextLarge
import com.example.techquiz.ui.common.SpacedLazyVerticalGrid
import com.example.techquiz.ui.theme.CodingQuizTheme
import com.example.techquiz.viewmodel.GivenAnswerViewModel
import com.example.techquiz.viewmodel.QuestionViewModel
import com.example.techquiz.viewmodel.TimerViewModel
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import org.koin.androidx.compose.koinViewModel
import org.koin.core.parameter.parametersOf
import kotlin.time.Duration.Companion.seconds

class AnswerState {
    var isAnyAnswerChosen: Boolean by mutableStateOf(false)
    var isTimeOut: Boolean by mutableStateOf(false)
}

@Composable
fun QuestionScreen(
    questionViewModel: QuestionViewModel = koinViewModel(),
    givenAnswerViewModel: GivenAnswerViewModel = koinViewModel(),
    timerViewModel: TimerViewModel = koinViewModel { parametersOf(QuestionViewModel.TIMEOUT) },
    category: Category,
    onBackPressed: (List<QuizResult>) -> Unit,
    navigateToResults: (List<QuizResult>) -> Unit,
) {
    val coroutineScope = rememberCoroutineScope()
    val questionResult by questionViewModel.question.collectAsStateWithLifecycle()
    val answerAddResult by givenAnswerViewModel.answerAddResult.collectAsStateWithLifecycle()
    var question by remember {
        mutableStateOf(QuestionViewModel.DEFAULT_QUESTION)
    }
    val questionNumber by questionViewModel.questionNumber.collectAsStateWithLifecycle()
    val timeLeft by timerViewModel.timeLeft.collectAsStateWithLifecycle()

    val answerState by remember {
        mutableStateOf(AnswerState())
    }

    val answerAddCallback = {
        if (questionViewModel.isQuestionLast()) {
            navigateToResults(givenAnswerViewModel.quizResults)
        } else {
            answerState.isAnyAnswerChosen = false
            answerState.isTimeOut = false
            questionViewModel.nextQuestion()
        }
    }

    LaunchedEffect(questionResult) {
        questionResult.fold(
            onSuccess = {
                if (it != QuestionViewModel.DEFAULT_QUESTION) {
                    question = it
                    timerViewModel.start()
                }
            },
            onFailure = {
                // TODO
                Log.e("Demo failure", it.stackTraceToString())
            },
        )
    }

    LaunchedEffect(answerAddResult) {
        answerAddResult?.fold(
            onSuccess = {
                delay(1.seconds)
                answerAddCallback()
            },
            onFailure = {
                // TODO
                Log.e("Demo failure", it.stackTraceToString())
            }
        )
    }

    BackHandler {
        onBackPressed(givenAnswerViewModel.quizResults)
    }

    CodingQuizTheme {
        LaunchedEffect(Unit) {
            questionViewModel.fetchQuestions(category)
        }
        Column(
            modifier = Modifier.padding(12.dp),
            verticalArrangement = Arrangement.SpaceBetween,
        ) {
            QuestionHeader(
                categoryName = category.name,
                questionNumber = questionNumber,
            )
            QuestionText(question)
            AnswersGrid(
                answers = question.answers,
                { answerState.isAnyAnswerChosen },
                { answerState.isTimeOut },
            ) {
                if (!answerState.isAnyAnswerChosen) {
                    coroutineScope.launch {
                        answerState.isAnyAnswerChosen = true
                        timerViewModel.clear()
                        givenAnswerViewModel.addAnswer(
                            answer = GivenAnswer(
                                question = question,
                                correct = it.isCorrect,
                            ),
                        )
                    }
                }
            }
            Timer(timeLeft = timeLeft)
        }
    }

    if (timeLeft == 0L) {
        LaunchedEffect(Unit) {
            answerState.isTimeOut = true

            givenAnswerViewModel.addAnswer(
                answer = GivenAnswer(
                    question = question,
                    correct = false,
                ),
            )
        }
    }
}

@Composable
private fun QuestionHeader(
    categoryName: String,
    questionNumber: Int,
) {
    HeaderTextLarge(
        text = stringResource(id = R.string.question_header, categoryName, questionNumber),
    )
}

@Composable
private fun QuestionText(question: Question) {
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
    isAnyAnswerChosen: () -> Boolean,
    isTimeOut: () -> Boolean,
    onClick: (PossibleAnswer) -> Unit,
) {
    SpacedLazyVerticalGrid(
        columns = GridCells.Fixed(2),
    ) {
        items(answers) {
            val shouldChangeColor = (isAnyAnswerChosen() || isTimeOut())
            val color = if (shouldChangeColor && it.isCorrect) Color.Green
                else if (shouldChangeColor) Color.Red
                else Color.Gray

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


@Preview
@Composable
private fun PreviewQuestionText() {
    CodingQuizTheme {
        QuestionText(question = Question(0, Category("0"),
            "Lorem ipsum dolor sit amet, consectetur adipiscing elit, " +
                    "sed do eiusmod tempor incididunt ut labore et dolore " +
                    "magna aliqua. Ut enim ad minim veniam, quis nostrud " +
                    "exercitation ullamco laboris nisi ut aliquip ex ea " +
                    "commodo consequat. Duis aute irure dolor in " +
                    "reprehenderit in voluptate velit esse cillum dolore " +
                    "eu fugiat nulla pariatur. Excepteur sint occaecat " +
                    "cupidatat non proident, sunt in culpa qui officia " +
                    "deserunt mollit anim id est laborum.",
            emptyList()))
    }
}

@Preview
@Composable
private fun PreviewAnswers() {
    val isAnyAnswerChosen by remember { mutableStateOf(false) }
    val isTimeOut by remember { mutableStateOf(false) }

    CodingQuizTheme {
        AnswersGrid(
            isAnyAnswerChosen = { isAnyAnswerChosen },
            isTimeOut = { isTimeOut },
            answers = listOf(
                PossibleAnswer("Demo Answer 1", false),
                PossibleAnswer("Demo Answer 2", false),
                PossibleAnswer("Demo Answer 3", false),
                PossibleAnswer("Demo Answer 4", true),
            )
        ) {}
    }
}

@Preview(showBackground = true)
@Composable
private fun PreviewTimer() {
    CodingQuizTheme {
        Timer(timeLeft = 2137)
    }
}
