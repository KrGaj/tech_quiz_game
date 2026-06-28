package com.example.techquiz.di

import com.example.techquiz.app.ui.question.QuestionViewModel
import com.example.techquiz.data.domain.Category
import com.example.techquiz.viewmodel.CategoryViewModel
import com.example.techquiz.viewmodel.LoginViewModel
import com.example.techquiz.viewmodel.QuizResultsViewModel
import com.example.techquiz.viewmodel.StatsViewModel
import org.koin.core.module.dsl.viewModel
import org.koin.dsl.module
import org.koin.plugin.module.dsl.viewModel

val viewModelModule = module {
    includes(repositoryModule)

    viewModel<CategoryViewModel>()
    viewModel<LoginViewModel>()

    viewModel { (category: Category) ->
        QuestionViewModel(
            category = category,
            questionRepository = get(),
            userAnswerRepository = get(),
            userDataProvider = get(),
        )
    }

    viewModel<QuizResultsViewModel>()
    viewModel<StatsViewModel>()
}
