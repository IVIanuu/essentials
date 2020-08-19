package com.ivianuu.essentials.ui.common

import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.foundation.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material.Colors
import androidx.compose.material.MaterialTheme
import androidx.compose.material.Shapes
import androidx.compose.material.Typography
import com.ivianuu.essentials.ui.core.systemBarStyle

@Composable
fun EsMaterialTheme(
    colors: Colors = MaterialTheme.colors,
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
