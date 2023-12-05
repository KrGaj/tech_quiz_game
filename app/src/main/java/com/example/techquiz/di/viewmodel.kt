package com.example.techquiz.di

import com.example.techquiz.viewmodel.CategoryViewModel
import com.example.techquiz.viewmodel.GivenAnswerViewModel
import com.example.techquiz.viewmodel.QuestionViewModel
import com.example.techquiz.viewmodel.QuizResultsViewModel
import com.example.techquiz.viewmodel.StatsViewModel
import com.example.techquiz.viewmodel.TimerViewModel
import org.koin.androidx.viewmodel.dsl.viewModelOf
import org.koin.dsl.module

val viewModelModule = module {
    viewModelOf(::CategoryViewModel)
    viewModelOf(::QuestionViewModel)
    viewModelOf(::GivenAnswerViewModel)
    viewModelOf(::QuizResultsViewModel)
    viewModelOf(::StatsViewModel)
    viewModelOf(::TimerViewModel)
}
