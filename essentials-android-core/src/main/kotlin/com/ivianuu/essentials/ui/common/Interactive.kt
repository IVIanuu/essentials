package com.ivianuu.essentials.ui.common

import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.drawOpacity

@Composable
fun Modifier.interactive(interactive: Boolean): Modifier {
    return drawOpacity(opacity = if (interactive) 1f else 0.5f)
        .absorbPointer(absorb = !interactive)
}
