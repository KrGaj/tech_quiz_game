package com.example.techquiz.app

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import com.example.techquiz.app.ui.navigation.NavigationRoot
import com.example.techquiz.app.ui.navigation.Navigator
import com.example.techquiz.ui.screen.LoginScreen
import com.example.techquiz.ui.theme.CodingQuizTheme
import org.koin.android.ext.android.inject
import org.koin.android.scope.AndroidScopeComponent
import org.koin.androidx.compose.navigation3.getEntryProvider
import org.koin.androidx.scope.activityRetainedScope
import org.koin.core.annotation.KoinExperimentalAPI

class MainActivity : ComponentActivity(), AndroidScopeComponent {
    override val scope by activityRetainedScope()

    @OptIn(KoinExperimentalAPI::class)
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()

        setContent {
            val navigator by inject<Navigator>()

            CodingQuizTheme {
                var isLoggedIn by rememberSaveable {
                    mutableStateOf(false)
                }
                // TODO change when refactoring login
                if (!isLoggedIn) {
                    LoginScreen {
                        isLoggedIn = true
                    }
                } else {
                    NavigationRoot(
                        navigator = navigator,
                        entryProvider = getEntryProvider(),
                    )
                }
            }
        }
    }
}