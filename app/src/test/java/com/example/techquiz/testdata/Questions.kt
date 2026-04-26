package com.example.techquiz.testdata

import com.example.techquiz.data.domain.Category
import com.example.techquiz.domain.models.AnswerOption
import com.example.techquiz.domain.models.Question
import kotlin.time.Duration.Companion.seconds

object Questions {
    val timeout = 5.seconds

    val category = Category(
        id = 1,
        name = "Demo Category",
    )

    val questions = listOf(
        Question(
            category = category,
            text = "Question ABC",
            options = listOf(
                AnswerOption(
                    text = "Yes",
                    isCorrect = true,
                ),
                AnswerOption(
                    text = "No",
                    isCorrect = false,
                ),
                AnswerOption(
                    text = "Maybe",
                    isCorrect = true,
                ),
                AnswerOption(
                    text = "It depends",
                    isCorrect = false,
                ),
            ),
        ),
        Question(
            category = category,
            text = "Question xD",
            options = listOf(
                AnswerOption(
                    text = "A",
                    isCorrect = false,
                ),
                AnswerOption(
                    text = "B",
                    isCorrect = true,
                ),
                AnswerOption(
                    text = "C",
                    isCorrect = false,
                ),
                AnswerOption(
                    text = "Zero",
                    isCorrect = false,
                ),
            ),
        ),
        Question(
            category = category,
            text = "Example Question",
            options = listOf(
                AnswerOption(
                    text = "1",
                    isCorrect = true,
                ),
                AnswerOption(
                    text = "2",
                    isCorrect = false,
                ),
                AnswerOption(
                    text = "All",
                    isCorrect = false,
                ),
                AnswerOption(
                    text = "Yes",
                    isCorrect = true,
                ),
            ),
        ),
        Question(
            category = category,
            text = "Question DEF",
            options = listOf(
                AnswerOption(
                    text = "Up",
                    isCorrect = true,
                ),
                AnswerOption(
                    text = "Down",
                    isCorrect = false,
                ),
                AnswerOption(
                    text = "Sing 'Barka'",
                    isCorrect = true,
                ),
                AnswerOption(
                    text = "Stop",
                    isCorrect = false,
                ),
            ),
        ),
        Question(
            category = category,
            text = "Question MZ ETZ 251",
            options = listOf(
                AnswerOption(
                    text = "Slow",
                    isCorrect = false,
                ),
                AnswerOption(
                    text = "Fast",
                    isCorrect = true,
                ),
                AnswerOption(
                    text = "Of course it's fast",
                    isCorrect = true,
                ),
                AnswerOption(
                    text = "Jawa 350 TS is better",
                    isCorrect = false,
                ),
            ),
        ),
    )
}
