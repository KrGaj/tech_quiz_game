package com.example.techquiz.data.domain

import android.os.Parcelable
import kotlinx.parcelize.Parcelize
import kotlinx.serialization.Serializable

@Serializable
@Parcelize
data class UserAnswer(
    val question: Question = Question(),
    val selectedOptions: List<AnswerOption> = emptyList(),
) : Parcelable {
    val isCorrect
        get() = question.options.filter { it.isCorrect } == selectedOptions
}
