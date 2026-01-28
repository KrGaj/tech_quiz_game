package com.example.techquiz.di

import com.example.techquiz.app.ui.question.QuestionViewModel
import com.example.techquiz.data.domain.Category
import com.example.techquiz.viewmodel.CategoryViewModel
import com.example.techquiz.viewmodel.LoginViewModel
import com.example.techquiz.viewmodel.QuizResultsViewModel
import com.example.techquiz.viewmodel.StatsViewModel
import org.koin.core.module.dsl.viewModel
import org.koin.core.module.dsl.viewModelOf
import org.koin.dsl.module

val viewModelModule = module {
    viewModelOf(::CategoryViewModel)
    viewModelOf(::LoginViewModel)

    viewModel { (category: Category) ->
        QuestionViewModel(
            category = category,
            questionRepository = get(),
            userAnswerRepository = get(),
            userDataStoreRepository = get(),
        )
    }

    viewModelOf(::QuizResultsViewModel)
    viewModelOf(::StatsViewModel)
}
