package com.example.techquiz.data.domain

import kotlinx.serialization.Serializable

@Serializable
data class CategoryStats(
    val category: Category,
    val answersGiven: Int,
)
