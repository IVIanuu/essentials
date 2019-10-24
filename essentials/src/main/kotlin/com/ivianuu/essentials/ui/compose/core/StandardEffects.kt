package com.ivianuu.essentials.ui.compose.core

import androidx.compose.CommitScope
import androidx.compose.effectOf

fun onActive(
    vararg inputs: Any?,
    callback: CommitScope.() -> Unit
) = effectOf<Unit> {
    composable(key = inputs.contentHashCode()) {
        +androidx.compose.onActive(callback)
    }
}