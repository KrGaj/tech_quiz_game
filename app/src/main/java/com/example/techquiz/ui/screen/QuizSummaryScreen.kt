package com.example.techquiz.ui.screen

import androidx.activity.compose.BackHandler
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.IntrinsicSize
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.Card
import androidx.compose.material3.FilledTonalButton
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.example.techquiz.R
import com.example.techquiz.data.domain.QuizResult
import com.example.techquiz.ui.theme.CodingQuizTheme
import com.example.techquiz.viewmodel.QuizResultsViewModel
import org.koin.androidx.compose.koinViewModel
import org.koin.core.parameter.parametersOf

@Composable
fun QuizSummaryScreen(
    quizResults: List<QuizResult>,
    quizResultsViewModel: QuizResultsViewModel = koinViewModel { parametersOf(quizResults) },
    onBackPressed: () -> Unit,
    navigateToCategories: () -> Unit,
) {
    BackHandler {
        onBackPressed()
    }

    CodingQuizTheme {
        Column(
            modifier = Modifier.padding(12.dp),
            verticalArrangement = Arrangement.SpaceBetween,
        ) {
            with(quizResultsViewModel) {
                Score(
                    correctAnswers = correctAnswersCount,
                    allAnswers = answersCount,
                )
                QuizResultsList(results = this.quizResults)
            }

            FinishButton(navigateToCategories)
        }
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
private fun QuizResultsList(results: List<QuizResult>) {
    LazyColumn(
        verticalArrangement = Arrangement.spacedBy(8.dp),
    ) {
        items(results) {
            QuizResult(it)
        }
    }
}

@Composable
private fun QuizResult(quizResult: QuizResult) {
    val answerIconResources = if (quizResult.isAnswerCorrect) {
        Pair(
            painterResource(id = R.drawable.ic_result_answer_correct),
            stringResource(id = R.string.quiz_results_correct),
        )
    } else {
        Pair(
            painterResource(id = R.drawable.ic_result_answer_wrong),
            stringResource(id = R.string.quiz_results_wrong),
        )
    }

    Card {
        Row(
            modifier = Modifier
                .padding(8.dp)
                .height(intrinsicSize = IntrinsicSize.Max),
            horizontalArrangement = Arrangement.spacedBy(8.dp),
        ) {
            Text(
                modifier = Modifier
                    .weight(0.8f)
                    .align(Alignment.CenterVertically),
                text = quizResult.questionText,
            )

            Icon(
                modifier = Modifier
                    .align(Alignment.CenterVertically),
                painter = answerIconResources.first,
                contentDescription = answerIconResources.second,
            )
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


@Preview
@Composable
private fun PreviewScore() {
    CodingQuizTheme {
        Score(correctAnswers = 21, allAnswers = 37)
    }
}

@Preview(showBackground = true)
@Composable
private fun PreviewQuizResultsList() {
    CodingQuizTheme {
        QuizResultsList(
            results = quizResults,
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

private val quizResults = listOf(
    QuizResult(
        "Demo Question 1",
        false,
    ),
    QuizResult(
        "Demo Question 2",
        true,
    ),
    QuizResult(
        "Demo Question 3, but very very very long for preview purposes",
        true,
    ),
)
