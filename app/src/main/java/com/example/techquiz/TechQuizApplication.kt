package com.example.techquiz

import android.app.Application
import com.example.techquiz.di.authModule
import com.example.techquiz.di.httpClientModule
import com.example.techquiz.di.repositoryModule
import com.example.techquiz.di.sslManagerModule
import com.example.techquiz.di.viewModelModule
import org.koin.android.ext.koin.androidContext
import org.koin.android.ext.koin.androidLogger
import org.koin.core.context.startKoin

class TechQuizApplication : Application() {
    override fun onCreate() {
        super.onCreate()

        startKoin {
            androidContext(this@TechQuizApplication)
            androidLogger()

            modules(
                listOf(
                    authModule,
                    httpClientModule,
                    sslManagerModule,
                    repositoryModule,
                    viewModelModule,
                )
            )
        }
    }
}