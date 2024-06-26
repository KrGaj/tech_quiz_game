package com.example.techquiz.util.navtype

import android.os.Bundle
import androidx.navigation.NavType
import com.example.techquiz.data.domain.QuizSummary
import kotlinx.serialization.json.Json

object QuizSummaryNavType : NavType<QuizSummary>(isNullableAllowed = false) {
    override fun get(bundle: Bundle, key: String): QuizSummary? =
        if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.TIRAMISU)
            bundle.getParcelable(key, QuizSummary::class.java)
        else bundle.getParcelable(key)

    override fun parseValue(value: String): QuizSummary =
        Json.decodeFromString(value)

    override fun put(bundle: Bundle, key: String, value: QuizSummary) =
        bundle.putParcelable(key, value)
}
