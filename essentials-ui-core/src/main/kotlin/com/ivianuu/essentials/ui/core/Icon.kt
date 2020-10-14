package com.ivianuu.essentials.ui.core

import androidx.compose.foundation.AmbientContentColor
import androidx.compose.foundation.Icon
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.VectorPainter
import androidx.compose.ui.res.vectorResource

@Composable
fun Icon(
    iconRes: Int,
    modifier: Modifier = Modifier,
    tint: Color = AmbientContentColor.current,
) {
    Icon(
        painter = VectorPainter(vectorResource(iconRes)),
        modifier = modifier,
        tint = tint
    )
}
