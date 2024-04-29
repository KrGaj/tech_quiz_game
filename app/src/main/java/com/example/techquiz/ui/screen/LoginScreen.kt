package com.example.techquiz.ui.screen

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Button
import androidx.compose.material3.Scaffold
import androidx.compose.material3.SnackbarHost
import androidx.compose.material3.SnackbarHostState
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.credentials.exceptions.GetCredentialCancellationException
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.example.techquiz.R
import com.example.techquiz.util.koinActivityViewModel
import com.example.techquiz.viewmodel.LoginViewModel
import com.example.techquiz.viewmodel.UserViewModel
import kotlinx.coroutines.launch
import org.koin.androidx.compose.koinViewModel

@Composable
fun LoginScreen(
    userViewModel: UserViewModel = koinActivityViewModel(),
    loginViewModel: LoginViewModel = koinViewModel(),
    navigateToCategories: () -> Unit,
) {
    val authResult by loginViewModel.authResult.collectAsStateWithLifecycle(initialValue = null)
    val snackbarHostState = remember {
        SnackbarHostState()
    }
    val coroutineScope = rememberCoroutineScope()

    LaunchedEffect(authResult) {
        authResult?.fold(
            onSuccess = {
                userViewModel.setCredential(it)
                val user = loginViewModel.fetchUser(userViewModel.token)
                userViewModel.userUuid = user.uuid
                navigateToCategories()
            },
            onFailure = {
                when(it) {  // TODO improve
                    is GetCredentialCancellationException -> Unit
                    else -> handleFailure(
                        snackbarHostState = snackbarHostState,
                        exception = it,
                    )
                }
            },
        )
    }

    // TODO improve
    Scaffold(
        snackbarHost = {
            SnackbarHost(hostState = snackbarHostState)
        }
    ) { paddingValues ->
        Column(
            modifier = Modifier
                .padding(paddingValues)
                .fillMaxSize(),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Center,
        ) {
            LaunchedEffect(Unit) {
                loginViewModel.startAuth()
            }

            Button(
                onClick = {
                    coroutineScope.launch {
                        loginViewModel.startAuth()
                    }
                }
            ) {
                Text(
                    text = stringResource(id = R.string.sign_in_button_label)
                )
            }
        }
    }
}

private suspend fun handleFailure(
    snackbarHostState: SnackbarHostState,
    exception: Throwable? = null,
) {
    snackbarHostState.showSnackbar(exception?.toString() ?: "Demo error")
}
