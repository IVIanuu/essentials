package com.ivianuu.essentials.gestures.action.ui

import androidx.compose.Composable
import com.ivianuu.essentials.gestures.action.Action
import com.ivianuu.essentials.ui.coroutines.collect
import com.ivianuu.essentials.ui.painter.DrawRenderable

@Composable
fun ActionIcon(action: Action) {
    val icon = collect(action.iconProvider.icon) // todo ir ?: return
    if (icon != null) DrawRenderable(renderable = icon)
}
