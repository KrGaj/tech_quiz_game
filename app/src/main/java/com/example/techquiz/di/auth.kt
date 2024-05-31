package com.example.techquiz.di

import androidx.credentials.CredentialManager
import org.koin.android.ext.koin.androidContext
import org.koin.dsl.module

val authModule = module {
    factory {
        CredentialManager.create(context = androidContext())
    }
}
