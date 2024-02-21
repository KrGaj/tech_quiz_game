package com.example.techquiz.viewmodel

import androidx.lifecycle.ViewModel
import com.example.techquiz.data.domain.QuizResult

class QuizResultsViewModel(
    val quizResults: List<QuizResult>,
) : ViewModel() {
    val correctAnswersCount
        get() = quizResults.count {
            it.givenAnswers.all { answer -> answer.isCorrect }
        }
    val answersCount
        get() = quizResults.count()
}
