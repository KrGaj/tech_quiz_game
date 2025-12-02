package com.example.techquiz.data.domain

import android.os.Parcelable
import kotlinx.parcelize.Parcelize

@Parcelize
data class QuizSummary(
    val results: List<UserAnswer>,
) : Parcelable

@Parcelize
data class UserAnswer(
    val question: Question = Question(),
    val selectedOptions: List<AnswerOption> = emptyList(),
    val isCorrect: Boolean = false,
) : Parcelable
