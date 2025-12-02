package com.example.techquiz.ui.screen

import androidx.activity.compose.BackHandler
import androidx.compose.animation.animateContentSize
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.IntrinsicSize
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material3.Card
import androidx.compose.material3.FilledTonalButton
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.example.techquiz.R
import com.example.techquiz.data.domain.PossibleAnswer
import com.example.techquiz.data.domain.Question
import com.example.techquiz.data.domain.GivenAnswer
import com.example.techquiz.ui.theme.CodingQuizTheme
import com.example.techquiz.viewmodel.QuizResultsViewModel
import org.koin.androidx.compose.koinViewModel
import org.koin.core.parameter.parametersOf

@Composable
fun QuizSummaryScreen(
    givenAnswers: List<GivenAnswer>,
    quizResultsViewModel: QuizResultsViewModel = koinViewModel { parametersOf(givenAnswers) },
    onBackPressed: () -> Unit,
    navigateToCategories: () -> Unit,
) {
    BackHandler {
        onBackPressed()
    }

    Column(
        modifier = Modifier
            .padding(12.dp),
        verticalArrangement = Arrangement.SpaceBetween,
    ) {
        with(quizResultsViewModel) {
            Score(
                correctAnswers = correctAnswersCount,
                allAnswers = answersCount,
            )
            QuizResultsList(results = this.givenAnswers)
        }

        FinishButton(navigateToCategories)
    }
}

@Composable
private fun Score(
    correctAnswers: Int,
    allAnswers: Int,
) {
    Card(
        modifier = Modifier.fillMaxWidth(),
    ) {
        val scoreText = stringResource(
            R.string.quiz_results_score,
            correctAnswers,
            allAnswers,
        )

        Text(
            modifier = Modifier
                .fillMaxWidth()
                .padding(8.dp),
            text = scoreText,
            textAlign = TextAlign.Center,
        )
    }
}

@Composable
private fun QuizResultsList(results: List<GivenAnswer>) {
    LazyColumn(
        verticalArrangement = Arrangement.spacedBy(8.dp),
    ) {
        items(results) {
            QuizResult(it)
        }
    }
}

@Composable
private fun QuizResult(givenAnswer: GivenAnswer) {
    var isExpanded by remember { mutableStateOf(false) }

    val answerCorrectColor = if (givenAnswer.isCorrect) {
        Color(0xFF81C784)
    } else {
        Color(0xFFEF5350)
    }

    Card {
        Column(
            modifier = Modifier
                .animateContentSize()
                .padding(8.dp)
                .clickable(
                    interactionSource = remember {
                        MutableInteractionSource()
                    },
                    indication = null,
                ) { isExpanded = !isExpanded },
        ) {
            Row(
                modifier = Modifier
                    .height(intrinsicSize = IntrinsicSize.Max),
                horizontalArrangement = Arrangement.spacedBy(8.dp),
            ) {
                Text(
                    modifier = Modifier
                        .weight(0.8f)
                        .align(Alignment.CenterVertically),
                    text = givenAnswer.question.text,
                )

                Column(
                    modifier = Modifier
                        .fillMaxHeight(),
                    verticalArrangement = Arrangement.Center,
                ) {
                    Box(
                        modifier = Modifier
                            .size(20.dp)
                            .clip(CircleShape)
                            .background(answerCorrectColor)
                            .align(Alignment.CenterHorizontally)
                    )
                }
            }

            if (isExpanded) {
                val correctAnswers = givenAnswer.question.answers.filter { it.isCorrect }
                val correctAnswersStr = buildAnswerString(
                    title = stringResource(
                        id = R.string.quiz_results_details_answers_correct,
                    ),
                    answers = correctAnswers,
                )

                val givenAnswersStr = buildAnswerString(
                    title = stringResource(
                        id = R.string.quiz_results_details_answers_given,
                    ),
                    answers = givenAnswer.selectedPossibleAnswers,
                )

                HorizontalDivider(
                    thickness = 2.dp,
                    color = Color.Black,
                )
                Text(text = correctAnswersStr)
                HorizontalDivider(
                    thickness = 2.dp,
                    color = Color.Black,
                )
                Text(text = givenAnswersStr)
            }
        }
    }
}

@Composable
private fun FinishButton(navigateToCategories: () -> Unit) {
    Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.Center,
    ) {
        FilledTonalButton(onClick = navigateToCategories) {
            Text(text = stringResource(id = R.string.quiz_results_finish))
        }
    }
}

private fun buildAnswerString(
    title: String,
    answers: List<PossibleAnswer>,
) = buildString {
    append(title)
    append(" ")
    append(
        answers.joinToString(separator = ", ") { it.text }
    )
}


@Preview(showBackground = true)
@Composable
private fun PreviewScore() {
    CodingQuizTheme {
        Score(
            correctAnswers = 21,
            allAnswers = 37,
        )
    }
}

@Preview(showBackground = true)
@Composable
private fun PreviewQuizResultsList() {
    CodingQuizTheme {
        QuizResultsList(
            results = givenAnswers,
        )
    }
}

@Preview(showBackground = true)
@Composable
private fun PreviewFinishButton() {
    CodingQuizTheme {
        FinishButton {}
    }
}

private val givenAnswers = listOf(
    GivenAnswer(
        question = Question(
            id = 0,
            category = com.example.techquiz.data.domain.Category("Demo"),
            text = "Demo Question 1",
            answers = listOf(
                PossibleAnswer(
                    text = "ABC",
                    isCorrect = false,
                ),
                PossibleAnswer(
                    text = "123",
                    isCorrect = true,
                ),
                PossibleAnswer(
                    text = "test",
                    isCorrect = true,
                )
            ),
        ),
        selectedPossibleAnswers = emptyList(),
        isCorrect = true,
    ),
    GivenAnswer(
        question = Question(
            id = 1,
            category = com.example.techquiz.data.domain.Category("Demo"),
            text = "Demo Question 2",
            answers = emptyList(),
        ),
        selectedPossibleAnswers = emptyList(),
        isCorrect = false,
    ),
    GivenAnswer(
        question = Question(
            id = 2,
            category = com.example.techquiz.data.domain.Category("Demo"),
            text = "Demo Question 3, but very very very long for preview purposes",
            answers = emptyList(),
        ),
        selectedPossibleAnswers = emptyList(),
        isCorrect = false,
    ),
)
