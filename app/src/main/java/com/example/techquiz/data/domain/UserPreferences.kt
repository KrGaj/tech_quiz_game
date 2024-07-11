package com.example.techquiz.data.domain

import java.util.UUID

data class UserPreferences(
    val userUUID: UUID,
    val userToken: String,
)
