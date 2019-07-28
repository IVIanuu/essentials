package com.ivianuu.essentials.ui.compose.view

import androidx.compose.ViewComposition
import androidx.ui.graphics.Color
import androidx.ui.material.themeColor

fun ViewComposition.SurfaceFrameLayout(
    color: Color = +themeColor { surface },
    children: ViewComposition.() -> Unit
) = FrameLayout {
    background(color)
    children()
}