package com.example.techquiz.app.ui.common

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.ExperimentalMaterial3ExpressiveApi
import androidx.compose.material3.LoadingIndicator
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import com.example.techquiz.ui.theme.CodingQuizTheme

@OptIn(ExperimentalMaterial3ExpressiveApi::class)
@Composable
fun LoadingScreen() {
    Box(
        modifier = Modifier.fillMaxSize(),
        contentAlignment = Alignment.Center,
    ) {
        // TODO consider using progress bar
        //  loading indicator should be used for operations taking 200ms - 5s
        LoadingIndicator()
    }
}


@Preview(showBackground = true)
@Composable
private fun PreviewLoadingScreen() {
    CodingQuizTheme {
        LoadingScreen()
    }
}
