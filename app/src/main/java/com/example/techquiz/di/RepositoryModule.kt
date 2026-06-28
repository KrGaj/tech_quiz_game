package com.example.techquiz.di

import com.example.techquiz.data.repository.CategoryRepositoryDefault
import com.example.techquiz.data.repository.StatsRepositoryDefault
import com.example.techquiz.data.repository.UserAnswerRepositoryDefault
import com.example.techquiz.data.repository.UserRepositoryDefault
import com.example.techquiz.domain.repository.CategoryRepository
import com.example.techquiz.domain.repository.StatsRepository
import com.example.techquiz.domain.repository.UserAnswerRepository
import com.example.techquiz.domain.repository.UserRepository
import org.koin.dsl.bind
import org.koin.dsl.module
import org.koin.plugin.module.dsl.factory

val repositoryModule = module {
    factory<CategoryRepositoryDefault>() bind CategoryRepository::class
    factory<UserAnswerRepositoryDefault>() bind UserAnswerRepository::class
    factory<StatsRepositoryDefault>() bind StatsRepository::class
    factory<UserRepositoryDefault>() bind UserRepository::class
}
