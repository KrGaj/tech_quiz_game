package com.example.techquiz.ui.common

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyListScope
import androidx.compose.runtime.Composable
import androidx.compose.ui.unit.dp

@Composable
fun SpacedLazyColumn(
    content: LazyListScope.() -> Unit,
) {
    LazyColumn(
        verticalArrangement = Arrangement.spacedBy(8.dp),
        content = content,
    )
}
