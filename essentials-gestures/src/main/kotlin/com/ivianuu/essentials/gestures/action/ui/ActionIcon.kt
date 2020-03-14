package com.ivianuu.essentials.gestures.action.ui

import androidx.compose.Composable
import com.ivianuu.essentials.gestures.action.Action
import com.ivianuu.essentials.ui.coroutines.collect

@Composable
fun ActionIcon(action: Action) {
    val icon = collect(action.iconProvider.icon)
    icon?.invoke()
}
