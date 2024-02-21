package com.example.techquiz.data.domain

import android.os.Parcelable
import kotlinx.parcelize.Parcelize
import kotlinx.serialization.Serializable

@Serializable
@Parcelize
data class PossibleAnswer(
    val text: String,
    val isCorrect: Boolean,
) : Parcelable
