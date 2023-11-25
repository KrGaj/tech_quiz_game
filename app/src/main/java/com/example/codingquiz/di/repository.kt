package com.example.codingquiz.di

import com.example.codingquiz.data.repository.GivenAnswerRepository
import com.example.codingquiz.data.repository.CategoryRepository
import com.example.codingquiz.data.repository.QuestionRepository
import com.example.codingquiz.data.repository.StatsRepository
import org.koin.core.module.dsl.singleOf
import org.koin.dsl.module

val repositoryModule = module {
    singleOf(::CategoryRepository)
    singleOf(::QuestionRepository)
    singleOf(::GivenAnswerRepository)
    singleOf(::StatsRepository)
}
