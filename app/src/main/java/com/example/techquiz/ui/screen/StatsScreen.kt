package com.example.techquiz.ui.screen

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.Scaffold
import androidx.compose.material3.SnackbarHost
import androidx.compose.material3.SnackbarHostState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.example.techquiz.R
import com.example.techquiz.data.domain.Category
import com.example.techquiz.data.dto.response.stats.CategoryStats
import com.example.techquiz.data.dto.response.stats.CorrectAnswersStats
import com.example.techquiz.ui.common.HeaderTextLarge
import com.example.techquiz.ui.common.HeaderTextMedium
import com.example.techquiz.ui.common.SpacedLazyColumn
import com.example.techquiz.ui.common.TwoTextsRow
import com.example.techquiz.ui.theme.CodingQuizTheme
import com.example.techquiz.util.getHttpFailureMessage
import com.example.techquiz.viewmodel.StatsViewModel
import com.valentinilk.shimmer.shimmer
import org.koin.androidx.compose.koinViewModel

@Composable
fun StatsScreen(
    statsViewModel: StatsViewModel = koinViewModel(),
) {
    val snackbarHostState = remember {
        SnackbarHostState()
    }
    val categoryStatsResult by statsViewModel.categoryStats
        .collectAsStateWithLifecycle()
    val correctAnswersStatsResult by statsViewModel.correctAnswersCount
        .collectAsStateWithLifecycle()

    var categoryStats by remember {
        mutableStateOf(emptyList<CategoryStats>())
    }

    var correctAnswersStats by remember {
        mutableStateOf(StatsViewModel.DEFAULT_CORRECT_STATS)
    }

    var isCategoryStatsLoading by remember {
        mutableStateOf(true)
    }

    var isCorrectAnswersStatsLoading by remember {
        mutableStateOf(true)
    }

    val context = LocalContext.current

    LaunchedEffect(categoryStatsResult) {
        categoryStatsResult?.fold(
            onSuccess = {
                categoryStats = it
                isCategoryStatsLoading = false
            },
            onFailure = {
                val messageRes = getHttpFailureMessage(it as? Exception)
                snackbarHostState.showSnackbar(context.getString(messageRes))
                isCategoryStatsLoading = false
            },
        )
    }

    LaunchedEffect(correctAnswersStatsResult) {
        correctAnswersStatsResult?.fold(
            onSuccess = {
                correctAnswersStats = it
                isCorrectAnswersStatsLoading = false
            },
            onFailure = {
                val messageRes = getHttpFailureMessage(it as? Exception)
                snackbarHostState.showSnackbar(context.getString(messageRes))
                isCorrectAnswersStatsLoading = false
            },
        )
    }

    LaunchedEffect(Unit) {
        statsViewModel.getMostAnsweredCategories()
        statsViewModel.getCorrectAnswersCount()
    }

    Scaffold(
        modifier = Modifier.padding(12.dp),
        snackbarHost = {
            SnackbarHost(hostState = snackbarHostState)
        }
    ) { paddingValues ->
        Column(
            modifier = Modifier.padding(paddingValues),
            verticalArrangement = Arrangement.spacedBy(20.dp),
        ) {
            StatsLabel()
            CategoryStatistics(
                isLoading = isCategoryStatsLoading,
                stats = categoryStats,
            )
            CorrectAnswersStatistics(
                isLoading = isCorrectAnswersStatsLoading,
                stats = correctAnswersStats,
            )
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
private fun CategoryStatistics(
    isLoading: Boolean,
    stats: List<CategoryStats>,
) {
    if (isLoading) {
        CategoryStatsLoading()
    } else {
        CategoryStatsLoaded(stats = stats)
    }
}

@Composable
private fun CorrectAnswersStatistics(
    isLoading: Boolean,
    stats: CorrectAnswersStats,
) {
    if (isLoading) {
        CorrectAnswersStatsLoading()
    } else {
        CorrectAnswersStatsLoaded(stats = stats)
    }
}

@Composable
private fun CategoryStatsLoading() {
    Column {
        CategoryStatsLabel()
        CategoryStatsListLoading()
    }
}

@Composable
private fun CategoryStatsListLoading() {
    SpacedLazyColumn {
        items(count = StatsViewModel.CATEGORIES_COUNT) {
            StatsRowLoading()
        }
    }
}

@Composable
private fun CorrectAnswersStatsLoading() {
    Column {
        CorrectAnswersStatsLabel()
        StatsRowLoading()
    }
}

@Composable
private fun StatsRowLoading() {
    TwoTextsRow(
        modifier = Modifier
            .shimmer(),
        leftText = "",
        rightText ="",
    )
}

@Composable
private fun CategoryStatsLoaded(stats: List<CategoryStats>) {
    Column {
        CategoryStatsLabel()
        CategoryStatsList(statsList = stats)
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
    SpacedLazyColumn {
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
private fun CorrectAnswersStatsLoaded(stats: CorrectAnswersStats) {
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


@Preview(showBackground = true)
@Composable
private fun PreviewCorrectAnswersStatsLoading() {
    CodingQuizTheme {
        CorrectAnswersStatsLoading()
    }
}

@Preview(showBackground = true)
@Composable
private fun PreviewCategoryStatsLoading() {
    CodingQuizTheme {
        CategoryStatsLoading()
    }
}

@Preview(showBackground = true)
@Composable
private fun PreviewCategoryStats() {
    CodingQuizTheme {
        CategoryStatsLoaded(stats = categoryStats)
    }
}

@Preview(showBackground = true)
@Composable
private fun PreviewCorrectAnswersStats() {
    CodingQuizTheme {
        CorrectAnswersStatsLoaded(stats = correctAnswersStats)
    }
}

private val categoryStats = listOf(
    CategoryStats(
        category = Category("Demo1"),
        2137,
    ),
    CategoryStats(
        category = Category("Demo2"),
        answersGiven = 21,
    ),
    CategoryStats(
        category = Category("Demo3"),
        answersGiven = 37,
    ),
)

private val correctAnswersStats = CorrectAnswersStats(
    correctAnswers = 20,
    allAnswers = 25,
)
