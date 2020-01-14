package com.ivianuu.essentials.gestures.action.ui

import androidx.compose.Composable
import com.ivianuu.essentials.gestures.action.Action
import com.ivianuu.essentials.ui.coroutines.collect
import com.ivianuu.essentials.ui.material.Icon
import com.ivianuu.essentials.ui.material.IconStyle

@Composable
fun ActionIcon(action: Action) {
    val icon = collect(action.iconProvider.icon) ?: return
    Icon(image = icon.image, style = IconStyle(tint = icon.tint))
}
