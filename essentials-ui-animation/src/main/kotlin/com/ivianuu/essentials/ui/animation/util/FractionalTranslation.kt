package com.ivianuu.essentials.ui.animation.util

import androidx.compose.runtime.*
import androidx.compose.ui.*
import androidx.compose.ui.graphics.*
import androidx.compose.ui.layout.*
import androidx.compose.ui.unit.*

fun Modifier.fractionalTranslation(
    translationXFraction: Float = 0f,
    translationYFraction: Float = 0f
) = composed {
    var size by remember { mutableStateOf(IntSize(Int.MAX_VALUE, Int.MAX_VALUE)) }
    onSizeChanged { size = it }
        .graphicsLayer {
            translationX = size.width * translationXFraction
            translationY = size.height * translationYFraction
        }
}