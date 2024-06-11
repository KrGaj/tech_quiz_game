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
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource
import androidx.credentials.exceptions.GetCredentialCancellationException
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.example.techquiz.R
import com.example.techquiz.util.getHttpFailureMessage
import com.example.techquiz.util.koinActivityViewModel
import com.example.techquiz.viewmodel.LoginViewModel
import com.example.techquiz.viewmodel.UserViewModel
import kotlinx.coroutines.launch
import org.koin.androidx.compose.koinViewModel
import org.koin.core.parameter.parametersOf

@Composable
fun LoginScreen(
    webClientId: String = stringResource(id = R.string.web_client_id),
    userViewModel: UserViewModel = koinActivityViewModel(),
    loginViewModel: LoginViewModel = koinViewModel { parametersOf(webClientId) },
    navigateToCategories: () -> Unit,
) {
    val authResult by loginViewModel.authResult.collectAsStateWithLifecycle(initialValue = null)
    val user by loginViewModel.user.collectAsStateWithLifecycle(initialValue = null)
    val snackbarHostState = remember {
        SnackbarHostState()
    }
    val context = LocalContext.current

    LaunchedEffect(authResult) {
        authResult?.fold(
            onSuccess = {
                userViewModel.setCredential(it)
                loginViewModel.fetchUser(userViewModel.token)
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
            onSuccess = {
                userViewModel.userUuid = it.uuid
                navigateToCategories()
            },
            onFailure = {
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
            AuthButton {
                loginViewModel.startAuth(context)
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
            .showSnackbar(throwable?.toString() ?: "Demo error")
    }
}

@Composable
private fun AuthButton(
    startAuth: suspend () -> Unit,
) {
    val coroutineScope = rememberCoroutineScope()

    Button(
        onClick = {
            coroutineScope.launch {
                startAuth()
            }
        }
    ) {
        Text(
            text = stringResource(id = R.string.sign_in_button_label)
        )
    }
}
