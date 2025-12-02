package com.example.techquiz.viewmodel

import androidx.lifecycle.ViewModel
import com.example.techquiz.data.domain.GivenAnswer

class QuizResultsViewModel(
    val givenAnswers: List<GivenAnswer>,
) : ViewModel() {
    val correctAnswersCount
        get() = givenAnswers.count { it.isCorrect }
    val answersCount
        get() = givenAnswers.count()
}
