package com.example.techquiz.data.domain

import android.os.Parcelable
import kotlinx.parcelize.Parcelize
import kotlinx.serialization.Serializable

@Serializable
@Parcelize
data class Question(
    val id: Int = 0,
    val category: Category = Category(name = "No category"),
    val text: String = "Question is not loaded yet",
    val options: List<AnswerOption> = emptyList(),
) : Parcelable
