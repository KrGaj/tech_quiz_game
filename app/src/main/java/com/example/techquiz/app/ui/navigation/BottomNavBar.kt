package com.example.techquiz.app.ui.navigation

import androidx.compose.material3.Icon
import androidx.compose.material3.NavigationBar
import androidx.compose.material3.NavigationBarItem
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource

@Composable
fun BottomNavBar(
    currentTab: NavTab,
    onClick: (NavTab) -> Unit,
) {
    NavigationBar {
        NavTab.entries.forEach {
            NavigationBarItem(
                selected = currentTab == it,
                icon = {
                    Icon(
                        painter = painterResource(it.iconResId),
                        contentDescription = stringResource(it.contentDescriptionResId),
                    )
                },
                label = {
                    Text(text = stringResource(it.labelResId))
                },
                onClick = {
                    onClick(it)
                },
            )
        }
    }
}
