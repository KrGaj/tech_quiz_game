package com.example.techquiz.domain.models

import kotlin.uuid.ExperimentalUuidApi
import kotlin.uuid.Uuid

@OptIn(ExperimentalUuidApi::class)
data class User(
    val uuid: Uuid,
)
