package com.example.techquiz.di

import com.example.techquiz.data.repository.CategoryRepository
import com.example.techquiz.data.repository.CategoryRepositoryDefault
import com.example.techquiz.data.repository.GivenAnswerRepository
import com.example.techquiz.data.repository.GivenAnswerRepositoryDefault
import com.example.techquiz.data.repository.QuestionRepository
import com.example.techquiz.data.repository.QuestionRepositoryDefault
import com.example.techquiz.data.repository.StatsRepository
import com.example.techquiz.data.repository.StatsRepositoryDefault
import com.example.techquiz.data.repository.UserDataStoreRepository
import com.example.techquiz.data.repository.UserRepository
import com.example.techquiz.data.repository.UserRepositoryDefault
import com.example.techquiz.viewmodel.CategoryViewModel
import com.example.techquiz.viewmodel.GivenAnswerViewModel
import com.example.techquiz.viewmodel.LoginViewModel
import com.example.techquiz.viewmodel.QuestionViewModel
import com.example.techquiz.viewmodel.StatsViewModel
import org.koin.core.module.Module
import org.koin.core.module.dsl.singleOf
import org.koin.core.qualifier.named
import org.koin.dsl.module

val repositoryModule = module {
    configureCategoryRepository()
    configureQuestionRepository()
    configureGivenAnswerRepository()
    configureStatsRepository()
    configureUserDataStoreRepository()
    configureUserRepository()
}

private fun Module.configureCategoryRepository() = scope<CategoryViewModel> {
    scoped<CategoryRepository> {
        CategoryRepositoryDefault(httpClient = get(named(QUIZ_API_CLIENT)))
    }
}

private fun Module.configureQuestionRepository() = scope<QuestionViewModel> {
    scoped<QuestionRepository> {
        QuestionRepositoryDefault(httpClient = get(named(QUIZ_API_CLIENT)))
    }
}

private fun Module.configureGivenAnswerRepository() = scope<GivenAnswerViewModel> {
    scoped<GivenAnswerRepository> {
        GivenAnswerRepositoryDefault(httpClient = get(named(TECH_QUIZ_BACKEND_CLIENT)))
    }
}

private fun Module.configureStatsRepository() = scope<StatsViewModel> {
    scoped<StatsRepository> {
        StatsRepositoryDefault(httpClient = get(named(TECH_QUIZ_BACKEND_CLIENT)))
    }
}

private fun Module.configureUserDataStoreRepository() =
    singleOf(::UserDataStoreRepository)

private fun Module.configureUserRepository() = scope<LoginViewModel> {
    scoped<UserRepository> {
        UserRepositoryDefault(httpClient = get(named(TECH_QUIZ_BACKEND_CLIENT)))
    }
}
