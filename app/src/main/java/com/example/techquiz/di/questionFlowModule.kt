package com.example.techquiz.di

import com.example.techquiz.data.QuestionFlow
import org.koin.core.module.dsl.factoryOf
import org.koin.dsl.module

val questionFlowModule = module {
    factoryOf(::QuestionFlow)
}
