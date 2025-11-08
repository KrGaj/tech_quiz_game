package com.example.techquiz

import android.app.Application
import com.example.techquiz.di.dataStoreModule
import com.example.techquiz.di.httpClientModule
import com.example.techquiz.di.propertiesReaderModule
import com.example.techquiz.di.repositoryModule
import com.example.techquiz.di.viewModelModule
import org.koin.android.ext.koin.androidContext
import org.koin.android.ext.koin.androidLogger
import org.koin.core.context.startKoin

class TechQuizApplication : Application() {
    private val koinModules = listOf(
        dataStoreModule,
        httpClientModule,
        propertiesReaderModule,
        repositoryModule,
        viewModelModule,
    )

    override fun onCreate() {
        super.onCreate()

        initKoin()
    }

    private fun initKoin() =
        startKoin {
            androidContext(this@TechQuizApplication)
            androidLogger()

            modules(koinModules)
        }
}
