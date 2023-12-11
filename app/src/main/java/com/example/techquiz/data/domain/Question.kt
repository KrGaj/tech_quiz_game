package com.example.techquiz.data.domain

data class Question(
    val id: Int,
    val category: String,
    val text: String,
    val answers: List<PossibleAnswer>,
)
