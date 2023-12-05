package com.example.techquiz.data.domain

data class Question(
    val id: Int,
    val categoryId: Int,
    val text: String,
    val answers: List<PossibleAnswer>,
)
