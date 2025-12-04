package com.example.techquiz.data.domain

import android.os.Parcelable
import kotlinx.parcelize.Parcelize
import kotlinx.serialization.Serializable

@Serializable
@Parcelize
data class AnswerOption(
    val text: String,
    val isCorrect: Boolean,
) : Parcelable
