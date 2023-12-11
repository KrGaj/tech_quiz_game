package com.example.techquiz.data.resources

import io.ktor.resources.Resource

@Resource("/questions")
class Questions(
    val category: String,
    val limit: Int,
)
