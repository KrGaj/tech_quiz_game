package com.example.techquiz.data.dto.request

import kotlinx.serialization.Serializable
import kotlin.uuid.ExperimentalUuidApi
import kotlin.uuid.Uuid

@Serializable
@OptIn(ExperimentalUuidApi::class)
data class GivenAnswerDTO(
    val userUuid: Uuid?,
    val question: QuestionReqDTO,
    val isCorrect: Boolean,
)
