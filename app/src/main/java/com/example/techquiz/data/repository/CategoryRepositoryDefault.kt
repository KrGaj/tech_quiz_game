package com.example.techquiz.data.repository

import androidx.compose.ui.text.capitalize
import androidx.compose.ui.text.intl.LocaleList
import com.example.techquiz.data.PossibleCategories
import com.example.techquiz.data.domain.Category

class CategoryRepositoryDefault : CategoryRepository {
    override fun getAllCategories(): List<Category> =
        PossibleCategories.entries.map {
            Category(it.name.capitalize(LocaleList(languageTags = "en")))
        }
}
