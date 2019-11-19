/*

Copyright 2019 Manuel Wrage
Licensed under the Apache License, Version 2.0 (the "License");
you may not use this file except in compliance with the License.
You may obtain a copy of the License at
http://www.apache.org/licenses/LICENSE-2.0
Unless required by applicable law or agreed to in writing, software
distributed under the License is distributed on an "AS IS" BASIS,
WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
See the License for the specific language governing permissions and
limitations under the License.
*/
package com.ivianuu.essentials.ui.compose.core

import androidx.compose.Composable
import androidx.compose.Composer
import androidx.compose.Effect
import androidx.compose.composer
import com.github.ajalt.timberkt.d
import com.ivianuu.essentials.util.sourceLocation

inline fun composable(noinline block: @Composable() () -> Unit) =
    composableWithKey(key = sourceLocation(), block = block)

fun composableWithKey(
    key: Any,
    block: @Composable() () -> Unit
) {
    with(composer.composer) {
        wrapInRestartScope(key) {
            startGroup(invocation)
            block()
            endGroup()
        }
    }
}

inline fun <V1> composable(
    v1: V1,
    noinline block: @Composable() () -> Unit
) = composableWithKey(key = sourceLocation(), v1 = v1, block = block)

fun <V1> composableWithKey(
    key: Any,
    v1: V1,
    block: @Composable() () -> Unit
) {
    with(composer.composer) {
        wrapInRestartScope(key) {
            if (changed(v1) || inserting) {
                startGroup(invocation)
                block()
                endGroup()
            } else {
                skipCurrentGroup()
            }
        }
    }
}

inline fun <V1, V2> composable(
    v1: V1,
    v2: V2,
    noinline block: @Composable() () -> Unit
) = composableWithKey(key = sourceLocation(), v1 = v1, v2 = v2, block = block)

fun <V1, V2> composableWithKey(
    key: Any,
    v1: V1,
    v2: V2,
    block: @Composable() () -> Unit
) {
    with(composer.composer) {
        wrapInRestartScope(key) {
            if (changed(v1) || changed(v2) || inserting) {
                startGroup(invocation)
                block()
                endGroup()
            } else {
                skipCurrentGroup()
            }
        }
    }
}

inline fun composable(
    vararg inputs: Any?,
    noinline block: @Composable() () -> Unit
) = composableWithKey(key = sourceLocation(), inputs = *inputs, block = block)

fun composableWithKey(
    key: Any,
    vararg inputs: Any?,
    block: @Composable() () -> Unit
) {
    with(composer.composer) {
        wrapInRestartScope(key) {
            if (changed(inputs.contentHashCode()) || inserting) {
                startGroup(invocation)
                block()
                endGroup()
            } else {
                skipCurrentGroup()
            }
        }
    }
}

inline fun staticComposable(noinline block: @Composable() () -> Unit) =
    staticComposableWithKey(key = sourceLocation(), block = block)

fun staticComposableWithKey(key: Any, block: @Composable() () -> Unit) {
    with(composer.composer) {
        wrapInRestartScope(key) {
            if (inserting) {
                startGroup(invocation)
                block()
                endGroup()
            } else {
                skipCurrentGroup()
            }
        }
    }
}

fun Composer<*>.wrapInRestartScope(key: Any, block: @Composable() () -> Unit) {
    startRestartGroup(key)
    block()
    endRestartGroup()?.updateScope { wrapInRestartScope(key, block) }
}

private val invocation = Any()

@BuilderInference
inline fun <T> effect(noinline block: @Composable() () -> T): T =
    effectWithKey(key = sourceLocation(), block = block)

@BuilderInference
fun <T> effectWithKey(
    key: Any,
    block: @Composable() () -> T
): T {
    d { "do effect " }
    with(composer.composer) {
        startGroup(key)
        val result = block()
        endGroup()
        return result
    }
}

inline operator fun <T> Effect<T>.invoke(): T = invoke(key = sourceLocation())

operator fun <T> Effect<T>.invoke(key: Any): T =
    resolve(androidx.compose.composer.composer, key.hashCode())