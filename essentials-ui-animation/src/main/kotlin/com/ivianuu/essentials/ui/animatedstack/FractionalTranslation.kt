package com.ivianuu.essentials.ui.animatedstack

import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.composed
import androidx.compose.ui.layout.onSizeChanged
import androidx.compose.ui.unit.IntSize
import androidx.compose.runtime.getValue
import androidx.compose.runtime.setValue
import androidx.compose.ui.graphics.graphicsLayer

fun Modifier.fractionalTranslation(
    translationXFraction: Float = 0f,
    translationYFraction: Float = 0f
) = composed {
    var size by remember { mutableStateOf(IntSize(Int.MAX_VALUE, Int.MAX_VALUE)) }
    onSizeChanged { size = it }
        .graphicsLayer {
            this.translationX = size.width * translationXFraction
            this.translationY = size.height * translationYFraction
        }
}
