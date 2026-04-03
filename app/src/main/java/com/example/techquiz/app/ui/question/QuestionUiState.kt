package com.example.techquiz.app.ui.question

import androidx.annotation.StringRes
import androidx.compose.runtime.Immutable
import com.example.techquiz.domain.models.AnswerOption
import com.example.techquiz.domain.models.UserAnswer

@Immutable
sealed class QuestionUiState {
    @Immutable
    data class Success(
        val question: QuestionDataUiState,
        val timeLeft: Long,
        val isExitDialogVisible: Boolean = false,
    ) : QuestionUiState()

    @Immutable
    data object Loading : QuestionUiState()

    @Immutable
    data class AnswersSent(
        val userAnswers: List<UserAnswer>,
    ) : QuestionUiState()

    @Immutable
    data object EmptyCategory : QuestionUiState()

    @Immutable
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
