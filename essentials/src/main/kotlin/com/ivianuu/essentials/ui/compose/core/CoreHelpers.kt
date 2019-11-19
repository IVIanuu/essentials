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

import androidx.compose.Ambient
import androidx.compose.CommitScope
import androidx.compose.Composable
import androidx.compose.Composer
import androidx.compose.Effect
import androidx.compose.composer
import com.github.ajalt.timberkt.d
import com.ivianuu.essentials.util.sourceLocation

fun composable(
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

fun <V1> composable(
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

fun <V1, V2> composable(
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

fun composable(
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

fun staticComposable(key: Any, block: @Composable() () -> Unit) {
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
    effect(key = sourceLocation(), block = block)

@BuilderInference
fun <T> effect(
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

inline fun <T, V1> key(
    v1: V1,
    noinline block: () -> T
) = androidx.compose.key<T, V1>(v1 = v1, block = { block() })()

inline fun <T, V1, V2> key(
    v1: V1,
    v2: V2,
    noinline block: () -> T
) = androidx.compose.key<T, V1, V2>(v1 = v1, v2 = v2, block = { block() })()

inline fun <T> key(
    vararg inputs: Any?,
    noinline block: () -> T
) = androidx.compose.key<T>(inputs = *inputs, block = { block() })()

inline fun <T> memo(noinline calculation: () -> T) =
    androidx.compose.memo(calculation = calculation)()

inline fun <T, V1> memo(
    v1: V1,
    noinline calculation: () -> T
) = androidx.compose.memo(v1 = v1, calculation = calculation)()

inline fun <T, V1, V2> memo(
    v1: V1,
    v2: V2,
    noinline calculation: () -> T
) = androidx.compose.memo(v1 = v1, v2 = v2, calculation = calculation)()

inline fun <T> memo(
    vararg inputs: Any?,
    noinline calculation: () -> T
) = androidx.compose.memo(inputs = *inputs, calculation = calculation)()

inline fun onActive(noinline callback: CommitScope.() -> Unit) =
    androidx.compose.onActive(callback = callback)()

inline fun onDispose(noinline callback: () -> Unit) =
    androidx.compose.onDispose(callback = callback)()

inline fun onCommit(noinline callback: CommitScope.() -> Unit) =
    androidx.compose.onCommit(callback = callback)()

inline fun <V1> onCommit(
    v1: V1,
    noinline callback: CommitScope.() -> Unit
) = androidx.compose.onCommit(v1 = v1, callback = callback)()

inline fun <V1, V2> onCommit(
    v1: V1,
    v2: V2,
    noinline callback: CommitScope.() -> Unit
) = androidx.compose.onCommit(v1 = v1, v2 = v2, callback = callback)()

inline fun onCommit(
    vararg inputs: Any?,
    noinline callback: CommitScope.() -> Unit
) = androidx.compose.onCommit(inputs = *inputs, callback = callback)()

inline fun onPreCommit(noinline callback: CommitScope.() -> Unit) =
    androidx.compose.onPreCommit(callback = callback)()

inline fun <V1> onPreCommit(
    v1: V1,
    noinline callback: CommitScope.() -> Unit
) = androidx.compose.onPreCommit(callback = callback)()

inline fun <V1, V2> onPreCommit(
    v1: V1,
    v2: V2,
    noinline callback: CommitScope.() -> Unit
) = androidx.compose.onPreCommit(v1 = v1, v2 = v2, callback = callback)()

inline fun onPreCommit(
    vararg inputs: Any?,
    noinline callback: CommitScope.() -> Unit
) = androidx.compose.onPreCommit(inputs = *inputs, callback = callback)()

inline fun <T> state(noinline init: () -> T) = androidx.compose.state(init = init)()

inline fun <T, V1> stateFor(v1: V1, noinline init: () -> T) =
    androidx.compose.stateFor(v1 = v1, init = init)()

inline fun <T, V1, V2> stateFor(
    v1: V1,
    v2: V2,
    noinline init: () -> T
) = androidx.compose.stateFor(v1 = v1, v2 = v2, init = init)()

inline fun <T> stateFor(vararg inputs: Any?, noinline init: () -> T) =
    androidx.compose.stateFor(inputs = *inputs, init = init)()


inline fun <T> model(noinline init: () -> T) = androidx.compose.model(init = init)()

inline fun <T, V1> modelFor(
    v1: V1,
    noinline init: () -> T
) = androidx.compose.modelFor(v1 = v1, init = init)()

inline fun <T, V1, V2> modelFor(
    v1: V1,
    v2: V2,
    noinline init: () -> T
) = androidx.compose.modelFor(v1 = v1, v2 = v2, init = init)()

inline fun <T> modelFor(
    vararg inputs: Any?,
    noinline init: () -> T
) = androidx.compose.modelFor(inputs = *inputs, init = init)()

inline fun <T> ambient(key: Ambient<T>) = androidx.compose.ambient(key = key)()

inline val invalidate get() = androidx.compose.invalidate()

inline fun compositionReference() = androidx.compose.compositionReference()()