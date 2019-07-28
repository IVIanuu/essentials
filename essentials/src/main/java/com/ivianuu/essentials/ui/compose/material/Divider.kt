package com.ivianuu.essentials.ui.compose.material

import android.view.View
import androidx.compose.ViewComposition
import androidx.compose.sourceLocation
import androidx.ui.core.Dp
import androidx.ui.core.dp
import androidx.ui.graphics.Color
import androidx.ui.material.themeColor
import com.ivianuu.essentials.ui.compose.view.View
import com.ivianuu.essentials.ui.compose.view.backgroundColor
import com.ivianuu.essentials.ui.compose.view.height
import com.ivianuu.essentials.ui.compose.view.margin

fun ViewComposition.Divider(
    color: Color = +themeColor { onSurface },
    height: Dp = 1.dp,
    indent: Dp = 0.dp
) = Divider(sourceLocation(), color, height, indent)

fun ViewComposition.Divider(
    key: Any,
    color: Color = +themeColor { onSurface },
    height: Dp = 1.dp,
    indent: Dp = 0.dp
) = View(key = key, ctor = { View(it) }) {
    height(height)
    margin(left = indent)
    backgroundColor(color)
}