package com.example.techquiz.app.ui.navigation

import androidx.annotation.DrawableRes
import androidx.annotation.StringRes
import com.example.techquiz.R

enum class NavTab(
    @param:DrawableRes val iconResId: Int,
    @param:StringRes val contentDescriptionResId: Int,
    @param:StringRes val labelResId: Int,
) {
    HOME(
        iconResId = R.drawable.home_24px,
        contentDescriptionResId = R.string.categories_icon_content_description,
        labelResId = R.string.navigation_categories_label,
    ),
    STATISTICS(
        iconResId = R.drawable.info_24px,
        contentDescriptionResId = R.string.stats_icon_content_description,
        labelResId = R.string.navigation_stats_label,
    ),
}
