package com.ivianuu.essentials.ui.common

import androidx.compose.Composable
import androidx.ui.core.Modifier
import androidx.ui.foundation.Box
import androidx.ui.layout.fillMaxSize
import androidx.ui.material.ColorPalette
import androidx.ui.material.MaterialTheme
import androidx.ui.material.Shapes
import androidx.ui.material.Typography
import com.ivianuu.essentials.ui.core.systemBarStyle

@Composable
fun EsMaterialTheme(
    colors: ColorPalette = MaterialTheme.colors,
    typography: Typography = MaterialTheme.typography,
    shapes: Shapes = MaterialTheme.shapes,
    content: @Composable () -> Unit
) {
    MaterialTheme(
        colors = colors,
        typography = typography,
        shapes = shapes
    ) {
        Box(
            modifier = Modifier.fillMaxSize()
                .systemBarStyle(),
            children = content
        )
    }
}
