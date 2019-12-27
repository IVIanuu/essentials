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

package com.ivianuu.essentials.coroutines

import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.emitAll
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.flow.onCompletion
import kotlinx.coroutines.flow.onEach

fun <T> Flow<T>.replayShareIn(scope: CoroutineScope): Flow<T> =
    replayShareImpl(scope = scope, defaultValue = Null)

fun <T> Flow<T>.replayShareIn(
    scope: CoroutineScope,
    defaultValue: T
): Flow<T> = replayShareImpl(scope = scope, defaultValue = defaultValue)

private fun <T> Flow<T>.replayShareImpl(
    scope: CoroutineScope,
    defaultValue: Any?
): Flow<T> {
    var lastValue: Any? = defaultValue

    val upstream = this
        .onEach { lastValue = it }
        .onCompletion { lastValue = defaultValue }
        .catch { lastValue = defaultValue }
        .shareIn(scope)

    return flow {
        if (lastValue !== Null) emit(lastValue as T)
        emitAll(upstream)
    }
}

internal object Null
