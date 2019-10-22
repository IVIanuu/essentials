package com.ivianuu.essentials.ui.compose.core

import androidx.compose.composer

@PublishedApi
internal val invocation = Any()

inline fun composable(
    key: Any,
    inputs: Array<Any?>? = null,
    block: () -> Unit
) {
    with(composer.composer) {
        startGroup(key)
        if (inserting || changed(inputs?.contentHashCode())) {
            startGroup(invocation)
            block()
            endGroup()
        } else {
            skipCurrentGroup()
        }
        endGroup()
    }
}