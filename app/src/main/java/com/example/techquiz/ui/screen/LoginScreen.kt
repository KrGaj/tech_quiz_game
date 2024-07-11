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
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.credentials.exceptions.GetCredentialCancellationException
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.example.techquiz.R
import com.example.techquiz.ui.theme.CodingQuizTheme
import com.example.techquiz.util.getHttpFailureMessage
import com.example.techquiz.viewmodel.LoginViewModel
import com.valentinilk.shimmer.shimmer
import kotlinx.coroutines.launch
import org.koin.androidx.compose.koinViewModel
import org.koin.core.parameter.parametersOf

@Composable
fun LoginScreen(
    webClientId: String = stringResource(id = R.string.web_client_id),
    loginViewModel: LoginViewModel = koinViewModel { parametersOf(webClientId) },
    navigateToCategories: () -> Unit,
) {
    val authResult by loginViewModel.authResult
        .collectAsStateWithLifecycle()
    val user by loginViewModel.user
        .collectAsStateWithLifecycle()
    val snackbarHostState = remember {
        SnackbarHostState()
    }
    val context = LocalContext.current
    var isLoading by remember {
        mutableStateOf(false)
    }

    LaunchedEffect(authResult) {
        authResult?.fold(
            onSuccess = { credential ->
                val token = credential.idToken

                isLoading = true
                loginViewModel.setToken(token)
                loginViewModel.fetchUser(token)
            },
            onFailure = {
                handleFailure(
                    snackbarHostState = snackbarHostState,
                    throwable = it,
                )
            },
        )
    }

    LaunchedEffect(user) {
        user?.fold(
            onSuccess = { user ->
                loginViewModel.setUserUUID(user.uuid)
                navigateToCategories()
            },
            onFailure = {
                isLoading = false
                val messageRes = getHttpFailureMessage(it as? Exception)
                snackbarHostState.showSnackbar(context.getString(messageRes))
            },
        )
    }

    Scaffold(
        snackbarHost = {
            SnackbarHost(hostState = snackbarHostState)
        },
    ) { paddingValues ->
        Column(
            modifier = Modifier
                .padding(paddingValues)
                .fillMaxSize(),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Center,
        ) {
            if (!isLoading) {
                AuthButton {
                    loginViewModel.startAuth(context)
                }
            } else {
                LoadingAuthButton()
            }

            LaunchedEffect(Unit) {
                loginViewModel.startAuth(context)
            }
        }
    }
}

private suspend fun handleFailure(
    snackbarHostState: SnackbarHostState,
    throwable: Throwable? = null,
) {
    when (throwable) {
        is GetCredentialCancellationException -> Unit
        else -> snackbarHostState
            .showSnackbar(throwable?.message ?: "Demo error")
    }
}

@Composable
private fun AuthButton(
    modifier: Modifier = Modifier,
    startAuth: suspend () -> Unit,
) {
    val coroutineScope = rememberCoroutineScope()

    Button(
        modifier = modifier,
        onClick = {
            coroutineScope.launch {
                startAuth()
            }
        }
    ) {
        AuthButtonText()
    }
}

@Composable
private fun LoadingAuthButton() {
    AuthButton(
        modifier = Modifier
            .shimmer()
    ) { }
}

@Composable
private fun AuthButtonText() {
    Text(
        text = stringResource(id = R.string.sign_in_button_label)
    )
}


@Preview(showBackground = true)
@Composable
private fun PreviewAuthButton() {
    CodingQuizTheme {
        AuthButton { }
    }
}

@Preview(showBackground = true)
@Composable
private fun PreviewLoadingAuthButton() {
    CodingQuizTheme {
        LoadingAuthButton()
    }
}
