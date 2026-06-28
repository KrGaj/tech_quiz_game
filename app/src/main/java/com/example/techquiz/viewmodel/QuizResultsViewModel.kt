package com.example.techquiz.viewmodel

import androidx.lifecycle.ViewModel
import com.example.techquiz.domain.models.UserAnswer

class QuizResultsViewModel(
    val userAnswers: List<UserAnswer>,
) : ViewModel() {
    val correctAnswersCount
        get() = userAnswers.count { it.isCorrect }
    val answersCount
        get() = userAnswers.count()
}
