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
import androidx.compose.Observe
import androidx.compose.composer
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
            if ((changed(v1) or changed(v2)) || inserting) {
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

inline fun (@Composable() () -> Unit).invokeAsComposable() = composable(this)

inline fun <V1> (@Composable() (V1) -> Unit).invokeAsComposable(v1: V1) = composable(v1 = v1) {
    this(v1)
}

inline fun <V1, V2> (@Composable() (V1, V2) -> Unit).invokeAsComposable(
    v1: V1,
    v2: V2
) = composable(v1, v2) {
    this(v1, v2)
}

inline fun <V1, V2, V3> (@Composable() (V1, V2, V3) -> Unit).invokeAsComposable(
    v1: V1,
    v2: V2,
    v3: V3
) = composable(v1, v2, v3) {
    this(v1, v2, v3)
}

inline fun <V1, V2, V3, V4> (@Composable() (V1, V2, V3, V4) -> Unit).invokeAsComposable(
    v1: V1,
    v2: V2,
    v3: V3,
    v4: V4
) = composable(v1, v2, v3, v4) {
    this(v1, v2, v3, v4)
}

inline fun <V1, V2, V3, V4, V5> (@Composable() (V1, V2, V3, V4, V5) -> Unit).invokeAsComposable(
    v1: V1,
    v2: V2,
    v3: V3,
    v4: V4,
    v5: V5
) = composable(v1, v2, v3, v4, v5) {
    this(v1, v2, v3, v4, v5)
}

inline fun <R> (@Composable() () -> R).invokeAsEffect(): R = effect(this)

inline fun <V1, R> (@Composable() (V1) -> R).invokeAsEffect(v1: V1): R = effect {
    this(v1)
}

inline fun <V1, V2, R> (@Composable() (V1, V2) -> R).invokeAsEffect(
    v1: V1,
    v2: V2
) = effect { this(v1, v2) }

inline fun <V1, V2, V3, R> (@Composable() (V1, V2, V3) -> R).invokeAsEffect(
    v1: V1,
    v2: V2,
    v3: V3
) = effect { this(v1, v2, v3) }

inline fun <V1, V2, V3, V4, R> (@Composable() (V1, V2, V3, V4) -> R).invokeAsEffect(
    v1: V1,
    v2: V2,
    v3: V3,
    v4: V4
) = effect { this(v1, v2, v3, v4) }

inline fun <V1, V2, V3, V4, V5, R> (@Composable() (V1, V2, V3, V4, V5) -> R).invokeAsEffect(
    v1: V1,
    v2: V2,
    v3: V3,
    v4: V4,
    v5: V5
) = effect { this(v1, v2, v3, v4, v5) }

@BuilderInference
inline fun <T> effect(noinline block: @Composable() () -> T): T =
    effectWithKey(key = sourceLocation(), block = block)

@BuilderInference
fun <T> effectWithKey(
    key: Any,
    block: @Composable() () -> T
): T {
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

fun composableLambda(block: @Composable() () -> Unit): @Composable() () -> Unit = {
    Observe(block)
}

fun <V1> composableLambda(block: @Composable() (V1) -> Unit): @Composable() (V1) -> Unit = { v1 ->
    Observe {
        block(v1)
    }
}

fun <V1, V2> composableLambda(block: @Composable() (V1, V2) -> Unit): @Composable() (V1, V2) -> Unit =
    { v1, v2 ->
        Observe {
            block(v1, v2)
        }
    }

fun <V1, V2, V3> composableLambda(block: @Composable() (V1, V2, V3) -> Unit): @Composable() (V1, V2, V3) -> Unit =
    { v1, v2, v3 ->
        Observe {
            block(v1, v2, v3)
        }
    }

fun <V1, V2, V3, V4> composableLambda(block: @Composable() (V1, V2, V3, V4) -> Unit): @Composable() (V1, V2, V3, V4) -> Unit =
    { v1, v2, v3, v4 ->
        Observe {
            block(v1, v2, v3, v4)
        }
    }

fun <V1, V2, V3, V4, V5> composableLambda(block: @Composable() (V1, V2, V3, V4, V5) -> Unit): @Composable() (V1, V2, V3, V4, V5) -> Unit =
    { v1, v2, v3, v4, v5 ->
        Observe {
            block(v1, v2, v3, v4, v5)
        }
    }
