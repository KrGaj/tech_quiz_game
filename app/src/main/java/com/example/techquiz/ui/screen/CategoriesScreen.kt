package com.example.techquiz.ui.screen

import androidx.activity.compose.BackHandler
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.FilledTonalButton
import androidx.compose.material3.MaterialTheme.typography
import androidx.compose.material3.SnackbarHostState
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.example.techquiz.R
import com.example.techquiz.data.domain.Category
import com.example.techquiz.ui.common.HeaderTextLarge
import com.example.techquiz.ui.common.SpacedLazyVerticalGrid
import com.example.techquiz.ui.dialogs.ExitDialog
import com.example.techquiz.ui.theme.CodingQuizTheme
import com.example.techquiz.util.findActivity
import com.example.techquiz.util.getHttpFailureMessage
import com.example.techquiz.util.toggleValue
import com.example.techquiz.viewmodel.CategoryViewModel
import io.ktor.client.plugins.ResponseException
import org.koin.androidx.compose.koinViewModel

@Composable
fun CategoriesScreen(
    categoryViewModel: CategoryViewModel = koinViewModel(),
    navigateToQuestionScreen: (Category) -> Unit,
) {
    val categoriesResult by categoryViewModel.categories.collectAsStateWithLifecycle()
    var categories by remember {
        mutableStateOf(emptyList<Category>())
    }
    val snackbarHostState = remember {
        SnackbarHostState()
    }
    val showExitAppDialog = rememberSaveable {
        mutableStateOf(false)
    }

    val context = LocalContext.current

    LaunchedEffect(categoriesResult) {
        categoriesResult.fold(
            onSuccess = { categories = it },
            onFailure = {
                val messageRes = getHttpFailureMessage(it as? ResponseException)
                snackbarHostState.showSnackbar(context.getString(messageRes))
            },
        )
    }

    BackHandler {
        showExitAppDialog.toggleValue()
    }

    if (showExitAppDialog.value) {
        ExitDialog(
            message = stringResource(id = R.string.app_exit_message),
            onDismissRequest = { showExitAppDialog.toggleValue() },
            onConfirmation = {
                showExitAppDialog.toggleValue()
                context.findActivity().finish()
            }
        )
    }

    CodingQuizTheme {
        Column(
            modifier = Modifier.padding(12.dp),
            verticalArrangement = Arrangement.spacedBy(20.dp),
        ) {
            LaunchedEffect(Unit) {
                categoryViewModel.fetchCategories()
            }
            CategoriesLabel()
            CategoryGrid(categories = categories, navigateToQuestionScreen)
        }
    }
}

@Composable
private fun CategoriesLabel() {
    HeaderTextLarge(
        text = stringResource(id = R.string.categories_header),
    )
}

@Composable
private fun CategoryGrid(
    categories: List<Category>,
    onItemClicked: (Category) -> Unit,
) {
    SpacedLazyVerticalGrid(
        columns = GridCells.Fixed(count = CategoryViewModel.COLUMNS_NUM),
    ) {
        items(categories) { item ->
            Category(
                name = item.name,
            ) {
                onItemClicked(item)
            }
        }
    }
}

@Composable
private fun Category(
    name: String,
    onClick: () -> Unit = {},
) {
    FilledTonalButton(
        shape = RoundedCornerShape(12.dp),
        onClick = onClick,
    ) {
        Text(
            text = name,
            modifier = Modifier
                .padding(8.dp)
                .fillMaxWidth(),
            textAlign = TextAlign.Center,
            style = typography.headlineMedium,
        )
    }
}

@Preview(showBackground = true, apiLevel = 33)
@Composable
private fun PreviewCategory() {
    CodingQuizTheme {
        Box(modifier = Modifier.width(200.dp)) {
            Category(name = "Demo")
        }
    }
}

@Preview(showBackground = true, apiLevel = 33)
@Composable
private fun PreviewCategoryGrid() {
    CodingQuizTheme {
        CategoryGrid(CATEGORIES) {}
    }
}

private val CATEGORIES = listOf(
    Category("Category 1"),
    Category("Category 2"),
    Category("Category 3"),
    Category("Category 4"),
)
