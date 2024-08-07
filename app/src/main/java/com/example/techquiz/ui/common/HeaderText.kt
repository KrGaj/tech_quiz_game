package com.example.techquiz.ui.common

import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier

@Composable
fun HeaderTextLarge(
    modifier: Modifier = Modifier,
    text: String,
) {
    Text(
        modifier = modifier,
        text = text,
        style = MaterialTheme.typography.headlineLarge,
    )
}

@Composable
fun HeaderTextMedium(
    modifier: Modifier = Modifier,
    text: String,
) {
    Text(
        text = text,
        style = MaterialTheme.typography.headlineMedium,
        modifier = modifier,
    )
}
