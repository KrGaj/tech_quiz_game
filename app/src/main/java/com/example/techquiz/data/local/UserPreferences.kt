package com.example.techquiz.data.local

import kotlin.uuid.ExperimentalUuidApi
import kotlin.uuid.Uuid

@OptIn(ExperimentalUuidApi::class)
data class UserPreferences(
    val userUuid: Uuid,
    val userToken: String,
)
