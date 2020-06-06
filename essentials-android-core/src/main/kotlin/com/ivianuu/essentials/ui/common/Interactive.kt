package com.ivianuu.essentials.ui.common

import androidx.compose.Composable
import androidx.ui.core.Modifier
import androidx.ui.core.drawOpacity

@Composable
fun Modifier.interactive(interactive: Boolean): Modifier {
    return drawOpacity(opacity = if (interactive) 1f else 0.5f)
        .absorbPointer(absorb = !interactive)
}
