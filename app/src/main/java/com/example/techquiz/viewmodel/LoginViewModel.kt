package com.example.techquiz.viewmodel

import android.app.Application
import androidx.credentials.CredentialManager
import androidx.credentials.CustomCredential
import androidx.credentials.GetCredentialRequest
import androidx.credentials.GetCredentialResponse
import androidx.credentials.exceptions.GetCredentialException
import androidx.lifecycle.AndroidViewModel
import com.example.techquiz.R
import com.example.techquiz.TechQuizApplication
import com.example.techquiz.data.domain.User
import com.example.techquiz.data.domain.exception.InvalidCredentialTypeException
import com.example.techquiz.data.repository.UserRepository
import com.example.techquiz.util.wrapAsResult
import com.google.android.libraries.identity.googleid.GetGoogleIdOption
import com.google.android.libraries.identity.googleid.GoogleIdTokenCredential
import com.google.android.libraries.identity.googleid.GoogleIdTokenParsingException
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.asSharedFlow

class LoginViewModel(
    application: Application,
    private val userRepository: UserRepository,
) : AndroidViewModel(application) {
    private val _authResult = MutableSharedFlow<Result<GoogleIdTokenCredential>>()
    val authResult
        get() = _authResult.asSharedFlow()

    private val _user = MutableSharedFlow<Result<User>>()
    val user
        get() = _user.asSharedFlow()

    private val credentialManager = CredentialManager.create(application.baseContext)
    private val webClientId = application.getString(R.string.web_client_id)

    private val googleIdOptionLogIn = GetGoogleIdOption.Builder()
        .setFilterByAuthorizedAccounts(true)
        .setServerClientId(webClientId)
        .build()

    private val googleIdOptionSignIn = GetGoogleIdOption.Builder()
        .setFilterByAuthorizedAccounts(false)
        .setServerClientId(webClientId)
        .build()

    suspend fun startAuth() {
        val logInResult = startAuth(AuthType.LOG_IN)
        var signInResult: Result<GoogleIdTokenCredential>? = null

        logInResult.fold(
            onSuccess = {
                _authResult.emit(Result.success(it))
            },
            onFailure = {
                signInResult = startAuth(AuthType.SIGN_IN)
            },
        )

        signInResult?.fold(
            onSuccess = {
                _authResult.emit(Result.success(it))
            },
            onFailure = {
                _authResult.emit(Result.failure(it))
            },
        )
    }

    private suspend fun startAuth(
        authType: AuthType,
    ): Result<GoogleIdTokenCredential> {
        val context = getApplication<TechQuizApplication>().baseContext

        val googleIdOption = when (authType) {
            AuthType.LOG_IN -> googleIdOptionLogIn
            AuthType.SIGN_IN -> googleIdOptionSignIn
        }

        val request = GetCredentialRequest.Builder()
            .addCredentialOption(googleIdOption)
            .build()

        return try {
            val response = credentialManager.getCredential(
                context = context,
                request = request,
            )

            handleSignIn(response)
        } catch (e: GetCredentialException) {
            Result.failure(e)
        }
    }

    private fun handleSignIn(
        result: GetCredentialResponse,
    ): Result<GoogleIdTokenCredential> {
        val credential = result.credential
        if (credential is CustomCredential
            && credential.type == GoogleIdTokenCredential.TYPE_GOOGLE_ID_TOKEN_CREDENTIAL) {
            try {
                val googleIdTokenCredential =
                    GoogleIdTokenCredential.createFrom(credential.data)
                return Result.success(googleIdTokenCredential)
            } catch (e: GoogleIdTokenParsingException) {
                return Result.failure(e)
            }
        } else {
            return Result.failure(InvalidCredentialTypeException())
        }
    }

    suspend fun fetchUser(
        token: String?,
    ): Unit = wrapAsResult {
        userRepository.getUser(token)
    }.let { _user.emit(it) }

    private enum class AuthType {
        LOG_IN,
        SIGN_IN,
    }
}
