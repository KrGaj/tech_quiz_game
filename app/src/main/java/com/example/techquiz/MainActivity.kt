package com.example.techquiz

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import com.example.techquiz.app.ui.navigation.NavigationRoot
import com.example.techquiz.ui.screen.LoginScreen
import com.example.techquiz.ui.theme.CodingQuizTheme

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()

        setContent {
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
                    NavigationRoot()
                }
            }
        }
    }
}
