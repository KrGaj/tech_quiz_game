package com.example.techquiz

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Surface
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.navigation.compose.currentBackStackEntryAsState
import androidx.navigation.compose.rememberNavController
import com.example.techquiz.di.httpClientModule
import com.example.techquiz.di.repositoryModule
import com.example.techquiz.di.sslManagerModule
import com.example.techquiz.di.viewModelModule
import com.example.techquiz.navigation.AppNavHost
import com.example.techquiz.navigation.BottomNavBar
import com.example.techquiz.navigation.Screen
import com.example.techquiz.ui.theme.CodingQuizTheme
import org.koin.android.ext.koin.androidContext
import org.koin.android.ext.koin.androidLogger
import org.koin.compose.KoinApplication

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        setContent {
            KoinApplication(application = {
                androidContext(application)
                androidLogger()

                modules(
                    listOf(
                        httpClientModule,
                        sslManagerModule,
                        repositoryModule,
                        viewModelModule,
                    )
                )
            }) {
                CodingQuizTheme {
                    val navController = rememberNavController()
                    val navBackStackEntry by navController.currentBackStackEntryAsState()

                    // A surface container using the 'background' color from the theme
                    Scaffold(
                        bottomBar = {
                            when (navBackStackEntry?.destination?.route) {
                                Screen.Categories.route,
                                Screen.Statistics.route -> BottomNavBar(
                                    navController = navController,
                                    destinationRoute = navBackStackEntry?.destination?.route,
                                )
                                else -> Unit
                            }
                        },
                    ) {
                        Surface(
                            modifier = Modifier
                                .fillMaxSize()
                                .padding(it),
                            color = MaterialTheme.colorScheme.background
                        ) {
                            AppNavHost(navController)
                        }
                    }
                }
            }
        }
    }
}
