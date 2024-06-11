package com.example.techquiz.data.domain

import android.os.Parcelable
import kotlinx.parcelize.Parcelize
import kotlinx.serialization.Serializable

@Serializable
@Parcelize
data class Question(
    val id: Int,
    val category: Category,
    val text: String,
    val answers: List<PossibleAnswer>,
) : Parcelable
