package com.ivianuu.essentials.gestures.action.ui

import androidx.compose.Composable
import androidx.compose.collectAsState
import com.ivianuu.essentials.gestures.action.Action

@Composable
fun ActionIcon(action: Action) {
    action.icon.collectAsState(null).value?.invoke()
}
