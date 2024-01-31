package com.example.techquiz.data.dto

import com.example.techquiz.util.UUIDSerializer
import kotlinx.serialization.Serializable
import java.util.UUID

@Serializable
data class GivenAnswerDTO(
    @Serializable(
        with = UUIDSerializer::class
    ) val userUUID: UUID,
    val question: QuestionDTO,
    val isCorrect: Boolean,
)
