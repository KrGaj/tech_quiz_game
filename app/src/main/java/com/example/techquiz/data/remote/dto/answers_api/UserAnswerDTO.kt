package com.example.techquiz.data.remote.dto.answers_api

import kotlinx.serialization.Serializable
import kotlin.uuid.ExperimentalUuidApi
import kotlin.uuid.Uuid

@Serializable
@OptIn(ExperimentalUuidApi::class)
data class UserAnswerDTO(
    val userUuid: Uuid?,
    val question: QuestionDTO,
    val isCorrect: Boolean,
)
