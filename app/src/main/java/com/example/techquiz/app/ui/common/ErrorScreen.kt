package com.example.techquiz.app.ui.common

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.example.techquiz.R
import com.example.techquiz.ui.common.ShapedFilledTonalButton
import com.example.techquiz.ui.theme.CodingQuizTheme

@Composable
fun ErrorScreen(
    errorMessage: String,
    onRetryClick: () -> Unit,
) {
    Box(
        modifier = Modifier.fillMaxSize(),
        contentAlignment = Alignment.Center,
    ) {
        Column(
            horizontalAlignment = Alignment.CenterHorizontally,
        ) {
            Text(
                text = errorMessage,
                style = MaterialTheme.typography.titleLarge
            )

            Spacer(
                modifier = Modifier.height(8.dp),
            )

            ShapedFilledTonalButton(
                onClick = onRetryClick,
            ) {
                Text(
                    text = stringResource(R.string.retry),
                    textAlign = TextAlign.Center,
                )
            }
        }
    }
}

@Preview(showBackground = true)
@Composable
private fun PreviewErrorScreen() {
    CodingQuizTheme {
        ErrorScreen(
            errorMessage = stringResource(R.string.client_error_message, 404),
            onRetryClick = {},
        )
    }
}
