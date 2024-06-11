package com.example.techquiz.di

import com.example.techquiz.viewmodel.CategoryViewModel
import com.example.techquiz.viewmodel.GivenAnswerViewModel
import com.example.techquiz.viewmodel.LoginViewModel
import com.example.techquiz.viewmodel.QuestionViewModel
import com.example.techquiz.viewmodel.QuizResultsViewModel
import com.example.techquiz.viewmodel.StatsViewModel
import com.example.techquiz.viewmodel.TimerViewModel
import com.example.techquiz.viewmodel.UserViewModel
import org.koin.androidx.viewmodel.dsl.viewModel
import org.koin.androidx.viewmodel.dsl.viewModelOf
import org.koin.dsl.module

val viewModelModule = module {
    viewModelOf(::CategoryViewModel)
    viewModelOf(::GivenAnswerViewModel)
    viewModelOf(::QuestionViewModel)
    viewModelOf(::QuizResultsViewModel)
    viewModelOf(::StatsViewModel)
    viewModelOf(::TimerViewModel)
    viewModelOf(::UserViewModel)

    viewModel { params -> LoginViewModel(params.get()) }
}
