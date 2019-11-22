/*
 * Copyright 2019 Manuel Wrage
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package com.ivianuu.essentials.ui.compose.core

import androidx.compose.Ambient
import androidx.compose.CommitScope
import androidx.compose.Composable

@Composable
fun <T, V1> key(
    v1: V1,
    block: () -> T
) = androidx.compose.key<T, V1>(v1 = v1, block = { block() })()

@Composable
fun <T, V1, V2> key(
    v1: V1,
    v2: V2,
    block: () -> T
) = androidx.compose.key<T, V1, V2>(v1 = v1, v2 = v2, block = { block() })()

@Composable
fun <T> key(
    vararg inputs: Any?,
    block: () -> T
) = androidx.compose.key<T>(inputs = *inputs, block = { block() })()

@Composable
fun <T> memo(calculation: () -> T) =
    androidx.compose.memo(calculation = calculation)()

@Composable
fun <T, V1> memo(
    v1: V1,
    calculation: () -> T
) = androidx.compose.memo(v1 = v1, calculation = calculation)()

@Composable
fun <T, V1, V2> memo(
    v1: V1,
    v2: V2,
    calculation: () -> T
) = androidx.compose.memo(v1 = v1, v2 = v2, calculation = calculation)()

@Composable
fun <T> memo(
    vararg inputs: Any?,
    calculation: () -> T
) = androidx.compose.memo(inputs = *inputs, calculation = calculation)()

@Composable
fun onActive(callback: CommitScope.() -> Unit) =
    androidx.compose.onActive(callback = callback)()

@Composable
fun onDispose(callback: () -> Unit) =
    androidx.compose.onDispose(callback = callback)()

@Composable
fun onCommit(callback: CommitScope.() -> Unit) =
    androidx.compose.onCommit(callback = callback)()

@Composable
fun <V1> onCommit(
    v1: V1,
    callback: CommitScope.() -> Unit
) = androidx.compose.onCommit(v1 = v1, callback = callback)()

@Composable
fun <V1, V2> onCommit(
    v1: V1,
    v2: V2,
    callback: CommitScope.() -> Unit
) = androidx.compose.onCommit(v1 = v1, v2 = v2, callback = callback)()

@Composable
fun onCommit(
    vararg inputs: Any?,
    callback: CommitScope.() -> Unit
) = androidx.compose.onCommit(inputs = *inputs, callback = callback)()

@Composable
fun onPreCommit(callback: CommitScope.() -> Unit) =
    androidx.compose.onPreCommit(callback = callback)()

@Composable
fun <V1> onPreCommit(
    v1: V1,
    callback: CommitScope.() -> Unit
) = androidx.compose.onPreCommit(callback = callback)()

@Composable
fun <V1, V2> onPreCommit(
    v1: V1,
    v2: V2,
    callback: CommitScope.() -> Unit
) = androidx.compose.onPreCommit(v1 = v1, v2 = v2, callback = callback)()

@Composable
fun onPreCommit(
    vararg inputs: Any?,
    callback: CommitScope.() -> Unit
) = androidx.compose.onPreCommit(inputs = *inputs, callback = callback)()

@Composable
fun <T> state(init: () -> T) = androidx.compose.state(init = init)()

@Composable
fun <T, V1> stateFor(v1: V1, init: () -> T) =
    androidx.compose.stateFor(v1 = v1, init = init)()

@Composable
fun <T, V1, V2> stateFor(
    v1: V1,
    v2: V2,
    init: () -> T
) = androidx.compose.stateFor(v1 = v1, v2 = v2, init = init)()

@Composable
fun <T> stateFor(vararg inputs: Any?, init: () -> T) =
    androidx.compose.stateFor(inputs = *inputs, init = init)()

@Composable
fun <T> model(init: () -> T) = androidx.compose.model(init = init)()

@Composable
fun <T, V1> modelFor(
    v1: V1,
    init: () -> T
) = androidx.compose.modelFor(v1 = v1, init = init)()

@Composable
fun <T, V1, V2> modelFor(
    v1: V1,
    v2: V2,
    init: () -> T
) = androidx.compose.modelFor(v1 = v1, v2 = v2, init = init)()

@Composable
fun <T> modelFor(
    vararg inputs: Any?,
    init: () -> T
) = androidx.compose.modelFor(inputs = *inputs, init = init)()

@Composable
fun <T> ambient(key: Ambient<T>): T = androidx.compose.ambient(key = key)()

/*@Composable*/
val invalidate get() = androidx.compose.invalidate()

@Composable
fun compositionReference() = androidx.compose.compositionReference()()