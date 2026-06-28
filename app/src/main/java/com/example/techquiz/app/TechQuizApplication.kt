package com.example.techquiz.app

import android.app.Application
import com.example.techquiz.app.di.QuizApp
import com.example.techquiz.app.di.navigationModule
import com.example.techquiz.di.dataStoreModule
import com.example.techquiz.di.propertiesReaderModule
import com.example.techquiz.di.repositoryModule
import com.example.techquiz.di.viewModelModule
import org.koin.android.ext.koin.androidContext
import org.koin.android.ext.koin.androidLogger
import org.koin.plugin.module.dsl.startKoin

class TechQuizApplication : Application() {
    private val koinModules = listOf(
        dataStoreModule,
        navigationModule,
        propertiesReaderModule,
        repositoryModule,
        viewModelModule,
    )

    override fun onCreate() {
        super.onCreate()

        initKoin()
    }

    private fun initKoin() =
        startKoin<QuizApp> {
            androidContext(this@TechQuizApplication)
            androidLogger()

            modules(koinModules)
        }
}
