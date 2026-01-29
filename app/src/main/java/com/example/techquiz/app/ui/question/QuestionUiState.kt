package com.example.techquiz.app.ui.question

import androidx.annotation.StringRes
import androidx.compose.runtime.Immutable
import com.example.techquiz.data.domain.AnswerOption
import com.example.techquiz.data.domain.UserAnswer

@Immutable
sealed class QuestionUiState {
    data class Success(
        val question: QuestionDataUiState,
        val timeLeft: Long,
        val isExitDialogVisible: Boolean = false,
    ) : QuestionUiState()

    data class Loading(
        val categoryName: String,
        val timeout: Long,
    ) : QuestionUiState()

    data object SendingAnswers : QuestionUiState()

    data class AnswersSent(
        val userAnswers: List<UserAnswer>,
    ) : QuestionUiState()

    data object EmptyCategory : QuestionUiState()

    data class Error(
        @param:StringRes val errorMsgRes: Int,
    ) : QuestionUiState()
}

@Immutable
data class QuestionDataUiState(
    val questionText: String,
    val categoryName: String,
    val options: List<AnswerOptionUiState>,
    val multipleCorrectAnswers: Boolean,
    val questionNumber: Int,
    val isLast: Boolean,
)

@Immutable
data class AnswerOptionUiState(
    val option: AnswerOption,
    val isSelected: Boolean,
)
