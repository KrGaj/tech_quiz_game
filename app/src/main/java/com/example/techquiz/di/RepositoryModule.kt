package com.example.techquiz.di

import com.example.techquiz.data.repository.CategoryRepository
import com.example.techquiz.data.repository.CategoryRepositoryDefault
import com.example.techquiz.data.repository.UserAnswerRepository
import com.example.techquiz.data.repository.UserAnswerRepositoryDefault
import com.example.techquiz.data.repository.QuestionRepository
import com.example.techquiz.data.repository.QuestionRepositoryDefault
import com.example.techquiz.data.repository.StatsRepository
import com.example.techquiz.data.repository.StatsRepositoryDefault
import com.example.techquiz.data.repository.UserDataStoreRepository
import com.example.techquiz.data.repository.UserRepository
import com.example.techquiz.data.repository.UserRepositoryDefault
import org.koin.core.module.dsl.factoryOf
import org.koin.core.qualifier.named
import org.koin.dsl.module

val repositoryModule = module {
    factory<CategoryRepository> {
        CategoryRepositoryDefault(
            httpClient = get(named(QUIZ_API_CLIENT)),
        )
    }

    factory<UserAnswerRepository> {
        UserAnswerRepositoryDefault(
            httpClient = get(named(TECH_QUIZ_BACKEND_CLIENT)),
        )
    }

    factory<QuestionRepository> {
        QuestionRepositoryDefault(
            httpClient = get(named(QUIZ_API_CLIENT)),
        )
    }

    factory<StatsRepository> {
        StatsRepositoryDefault(
            httpClient = get(named(TECH_QUIZ_BACKEND_CLIENT)),
        )
    }

    factory<UserRepository> {
        UserRepositoryDefault(
            httpClient = get(named(TECH_QUIZ_BACKEND_CLIENT)),
        )
    }

    factoryOf(::UserDataStoreRepository)
}
