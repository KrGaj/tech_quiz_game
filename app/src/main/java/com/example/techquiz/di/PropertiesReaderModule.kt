package com.example.techquiz.di

import org.koin.android.ext.koin.androidContext
import org.koin.dsl.module
import java.util.Properties

val propertiesReaderModule = module {
    factory {
        Properties().also {
            val propertiesStream = androidContext().assets.open(
                "env_config.properties",
            )
            it.load(propertiesStream)
        }
    }
}
