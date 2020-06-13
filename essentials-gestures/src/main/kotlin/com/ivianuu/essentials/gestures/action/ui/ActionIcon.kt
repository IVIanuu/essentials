package com.ivianuu.essentials.gestures.action.ui

import androidx.compose.Composable
import com.ivianuu.essentials.gestures.action.Action
import com.ivianuu.essentials.ui.coroutines.collectAsState

@Composable
fun ActionIcon(action: Action) {
    action.iconProvider.icon.collectAsState(null).value?.invoke()
}
