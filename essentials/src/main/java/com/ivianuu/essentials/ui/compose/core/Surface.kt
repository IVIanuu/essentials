package com.ivianuu.essentials.ui.compose.core

import androidx.compose.ViewComposition
import androidx.ui.core.CurrentTextStyleProvider
import androidx.ui.graphics.Color
import androidx.ui.material.MaterialColors
import androidx.ui.material.textColorForBackground
import androidx.ui.material.themeColor
import androidx.ui.text.TextStyle

fun ViewComposition.ThemeSurface(
    chooser: MaterialColors.() -> Color,
    children: ViewComposition.() -> Unit
) {
    Surface(+themeColor(chooser), children)
}

fun ViewComposition.Surface(
    color: Color,
    children: ViewComposition.() -> Unit
) {
    CurrentTextStyleProvider(value = TextStyle(color = +textColorForBackground(color))) {
        children()
    }
}