package com.ivianuu.essentials.gestures.action.ui

import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import com.ivianuu.essentials.gestures.action.Action

@Composable
fun ActionIcon(action: Action) {
    action.icon.collectAsState(null).value?.invoke()
}
