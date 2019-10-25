package com.ivianuu.essentials.ui.compose.core

import androidx.compose.composer

@PublishedApi
internal val invocation = Any()

inline fun <V1> composable(
    key: Any,
    v1: V1,
    block: () -> Unit
) {
    with(composer.composer) {
        startGroup(key)
        if (inserting || changed(v1)) {
            startGroup(invocation)
            block()
            endGroup()
        } else {
            skipCurrentGroup()
        }
        endGroup()
    }
}

inline fun <V1, V2> composable(
    key: Any,
    v1: V1,
    v2: V2,
    block: () -> Unit
) {
    with(composer.composer) {
        startGroup(key)
        if (inserting || changed(v1) || changed(v2)) {
            startGroup(invocation)
            block()
            endGroup()
        } else {
            skipCurrentGroup()
        }
        endGroup()
    }
}

inline fun composable(
    key: Any,
    vararg inputs: Array<Any?>,
    block: () -> Unit
) {
    with(composer.composer) {
        startGroup(key)
        if (inserting || inputs.any { changed(it) }) {
            startGroup(invocation)
            block()
            endGroup()
        } else {
            skipCurrentGroup()
        }
        endGroup()
    }
}

inline fun staticComposable(key: Any, block: () -> Unit) {
    composable(key = key, v1 = Unit, block = block)
}