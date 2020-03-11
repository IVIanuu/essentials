package com.ivianuu.essentials.android.ui.common

import androidx.compose.Composable
import androidx.compose.currentComposer

// todo better name
@Composable
inline fun ifChanged(vararg inputs: Any?, block: @Composable () -> Unit) {
    var valid = true
    for (input in inputs) valid = !currentComposer.changed(input) && valid
    if (!valid || !currentComposer.skipping) {
        currentComposer.startGroup(onlyOnce)
        block()
        currentComposer.endGroup()
    } else {
        currentComposer.skipCurrentGroup()
    }
}

@PublishedApi
internal val onlyOnce = Any()