package com.ivianuu.essentials.ui.compose.view

import android.view.View
import androidx.compose.ViewComposition
import androidx.ui.core.Dp
import androidx.ui.core.dp

fun ViewComposition.Spacer(width: Dp = 0.dp, height: Dp = 0.dp) =
    View<View> {
        width(width)
        height(height)
    }

fun ViewComposition.Spacer(size: Dp) = Spacer(size, size)

fun ViewComposition.WidthSpacer(width: Dp) = Spacer(width = width)
fun ViewComposition.HeightSpacer(height: Dp) = Spacer(height = height)