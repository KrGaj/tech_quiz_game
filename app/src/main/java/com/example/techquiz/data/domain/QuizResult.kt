package com.example.techquiz.data.domain

import android.os.Parcelable
import kotlinx.parcelize.Parcelize
import kotlinx.serialization.Serializable

@Serializable
@Parcelize
data class QuizSummary(
    val results: List<QuizResult>,
) : Parcelable

@Serializable
@Parcelize
data class QuizResult(
    val question: Question,
    val givenAnswers: List<PossibleAnswer>,
) : Parcelable
