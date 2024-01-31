package com.example.techquiz.data.domain

data class Question(
    val id: Int,
    val category: Category,
    val text: String,
    val answers: List<PossibleAnswer>,
)
