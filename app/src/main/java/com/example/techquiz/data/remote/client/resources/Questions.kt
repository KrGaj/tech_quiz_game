package com.example.techquiz.data.remote.client.resources

import io.ktor.resources.Resource

@Resource("/api.php")
class Questions(
    val category: Int,
    val amount: Int,
)
