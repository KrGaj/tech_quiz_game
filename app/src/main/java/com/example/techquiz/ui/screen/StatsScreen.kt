package com.example.techquiz.ui.screen

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.example.techquiz.R
import com.example.techquiz.data.dto.response.stats.CategoryStats
import com.example.techquiz.data.dto.response.stats.CorrectAnswersStats
import com.example.techquiz.ui.common.HeaderTextLarge
import com.example.techquiz.ui.common.HeaderTextMedium
import com.example.techquiz.ui.common.TwoTextsRow
import com.example.techquiz.ui.theme.CodingQuizTheme
import com.example.techquiz.util.koinActivityViewModel
import com.example.techquiz.viewmodel.StatsViewModel
import com.example.techquiz.viewmodel.UserViewModel
import org.koin.androidx.compose.koinViewModel

@Composable
fun StatsScreen(
    statsViewModel: StatsViewModel = koinViewModel(),
    userViewModel: UserViewModel = koinActivityViewModel(),
) {
    val categoryStats by statsViewModel.categoryStats
        .collectAsStateWithLifecycle()
    val correctAnswersStats by statsViewModel.correctAnswersCount
        .collectAsStateWithLifecycle()

    Column {
        LaunchedEffect(Unit) {
            statsViewModel.getMostAnsweredCategories(
                token = userViewModel.token,
                userUUID = userViewModel.userUuid,
            )
            statsViewModel.getCorrectAnswersCount(
                token = userViewModel.token,
                userUUID = userViewModel.userUuid,
            )
        }

        Column(
            modifier = Modifier.padding(12.dp),
            verticalArrangement = Arrangement.spacedBy(20.dp),
        ) {
            StatsLabel()
            CategoryStats(statsList = categoryStats)
            CorrectAnswersStats(stats = correctAnswersStats)
        }
    }
}

@Composable
private fun StatsLabel() {
    HeaderTextLarge(
        text = stringResource(id = R.string.stats_header),
    )
}

@Composable
private fun CategoryStats(statsList: List<CategoryStats>) {
    Column {
        CategoryStatsLabel()
        CategoryStatsList(statsList = statsList)
    }
}

@Composable
private fun CategoryStatsLabel() {
    HeaderTextMedium(
        text = stringResource(id = R.string.stats_most_answered_header),
    )
}

@Composable
private fun CategoryStatsList(statsList: List<CategoryStats>) {
    LazyColumn(
        verticalArrangement = Arrangement.spacedBy(8.dp),
    ) {
        val statsListSorted = statsList.sortedByDescending { it.answersGiven }
        items(statsListSorted) {
            CategoryStatsRow(it)
        }
    }
}

@Composable
private fun CategoryStatsRow(stats: CategoryStats) {
    TwoTextsRow(
        leftText = stats.category.name,
        rightText = stats.answersGiven.toString(),
    )
}

@Composable
private fun CorrectAnswersStats(stats: CorrectAnswersStats) {
    Column {
        CorrectAnswersStatsLabel()
        CorrectAnswersStatsRow(stats = stats)
    }
}

@Composable
private fun CorrectAnswersStatsLabel() {
    HeaderTextMedium(text = stringResource(id = R.string.correct_answers_header))
}

@Composable
private fun CorrectAnswersStatsRow(stats: CorrectAnswersStats) {
    val percentage = if (stats.allAnswers != 0L)
            (stats.correctAnswers.toDouble()/stats.allAnswers) * 100
    else 0.0

    TwoTextsRow(
        leftText = stringResource(id = R.string.correct_answers_msg),
        rightText = stringResource(
            id = R.string.correct_answers_count,
            stats.correctAnswers,
            stats.allAnswers,
            percentage,
        )
    )
}

@Preview(showBackground = true, apiLevel = 33)
@Composable
private fun PreviewCategoryStats() {
    CodingQuizTheme {
        CategoryStats(statsList = categoryStats)
    }
}

@Preview(showBackground = true, apiLevel = 33)
@Composable
private fun PreviewCorrectAnswersStats() {
    CodingQuizTheme {
        CorrectAnswersStats(stats = correctAnswersStats)
    }
}

private val categoryStats = listOf(
    CategoryStats(
        com.example.techquiz.data.domain.Category("Demo1"),
        2137,
    ),
    CategoryStats(
        com.example.techquiz.data.domain.Category("Demo2"),
        21,
    ),
    CategoryStats(
        com.example.techquiz.data.domain.Category("Demo3"),
        37,
    ),
)

private val correctAnswersStats = CorrectAnswersStats(20, 25)
