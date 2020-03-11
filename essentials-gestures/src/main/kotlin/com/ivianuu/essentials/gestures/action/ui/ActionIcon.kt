package com.ivianuu.essentials.gestures.action.ui

import androidx.compose.Composable
import com.ivianuu.essentials.android.ui.coroutines.collect
import com.ivianuu.essentials.gestures.action.Action

@Composable
fun ActionIcon(action: Action) {
    val icon = collect(action.iconProvider.icon)
    icon?.invoke()
}
