package com.ivianuu.essentials.ui.common

import androidx.compose.Composable
import androidx.compose.currentComposer

@Composable
inline fun <V1> skippable(v1: V1, block: @Composable () -> Unit) {
    if (currentComposer.changed(v1) || !currentComposer.skipping) {
        currentComposer.startGroup(skippable)
        block()
        currentComposer.endGroup()
    } else {
        currentComposer.skipCurrentGroup()
    }
}

@Composable
inline fun <V1, V2> skippable(v1: V1, v2: V2, block: @Composable () -> Unit) {
    if ((currentComposer.changed(v1) or currentComposer.changed(v2)) || !currentComposer.skipping) {
        currentComposer.startGroup(skippable)
        block()
        currentComposer.endGroup()
    } else {
        currentComposer.skipCurrentGroup()
    }
}

@Composable
inline fun skippable(vararg inputs: Any?, block: @Composable () -> Unit) {
    var valid = true
    for (input in inputs) valid = !currentComposer.changed(input) && valid
    if (!valid || !currentComposer.skipping) {
        currentComposer.startGroup(skippable)
        block()
        currentComposer.endGroup()
    } else {
        currentComposer.skipCurrentGroup()
    }
}

@PublishedApi
internal val skippable = Any()
