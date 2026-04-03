package com.example.techquiz.di

import com.example.techquiz.data.remote.client.BackendApiClient
import com.example.techquiz.data.remote.client.QuizApiClient
import org.koin.dsl.module
import org.koin.plugin.module.dsl.single

val httpClientModule = module {
    single<QuizApiClient>()
    single<BackendApiClient>()
}
