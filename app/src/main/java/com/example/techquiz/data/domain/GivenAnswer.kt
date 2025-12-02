package com.example.techquiz.data.domain

import android.os.Parcelable
import kotlinx.parcelize.Parcelize

@Parcelize
data class QuizSummary(
    val results: List<GivenAnswer>,
) : Parcelable

@Parcelize
data class GivenAnswer(
    val question: Question = Question(),
    val selectedPossibleAnswers: List<PossibleAnswer> = emptyList(),
    val isCorrect: Boolean = false,
) : Parcelable
