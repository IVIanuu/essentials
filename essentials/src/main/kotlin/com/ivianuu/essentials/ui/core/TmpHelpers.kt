package com.ivianuu.essentials.ui.core

import androidx.compose.Ambient
import androidx.compose.Composable
import androidx.compose.ViewValidator
import androidx.compose.ambient
import androidx.compose.currentComposerNonNull

inline fun tmpComposerCall(
    key: Any,
    invalid: ViewValidator.() -> Boolean,
    block: @Composable () -> Unit
) {
    with(currentComposerNonNull) {
        startGroup(key)
        if (ViewValidator(this).invalid() || inserting) {
            startGroup(0)
            block()
            endGroup()
        } else {
            skipCurrentGroup()
        }
        endGroup()
    }
}

inline fun <T> tmpComposerExpr(
    key: Any,
    block: @Composable () -> T
): T {
    with(currentComposerNonNull) {
        startExpr(key)
        val result = block()
        endExpr()
        return result
    }
}

inline fun tmpComposerJoinKey(
    left: Any?,
    right: Any?
): Any = currentComposerNonNull.joinKey(left, right)

@Composable
val <T> Ambient<T>.current: T
    get() = ambient(this)

fun <T> ambientOf(defaultFactory: () -> T): Ambient<T> = Ambient.of(defaultFactory)
