package com.example.techquiz.di

import android.content.Context
import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.preferencesDataStore
import org.koin.android.ext.koin.androidContext
import org.koin.dsl.module

private const val USER_PREFERENCES_NAME = "user_preferences"

val dataStoreModule = module {
    single<DataStore<Preferences>> {
        androidContext().userDataStore
    }
}

private val Context.userDataStore by preferencesDataStore(
    name = USER_PREFERENCES_NAME,
)
