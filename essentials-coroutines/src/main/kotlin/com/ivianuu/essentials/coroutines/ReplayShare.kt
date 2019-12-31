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
import kotlinx.coroutines.flow.onCompletion
import kotlinx.coroutines.flow.onEach
import kotlinx.coroutines.flow.onStart
import kotlin.time.Duration

fun <T> Flow<T>.replayShareIn(
    scope: CoroutineScope,
    timeout: Duration = Duration.ZERO,
    tag: String? = null
): Flow<T> = replayShareImpl(scope = scope, defaultValue = Null, timeout = timeout, tag = tag)

fun <T> Flow<T>.replayShareIn(
    scope: CoroutineScope,
    defaultValue: T,
    timeout: Duration = Duration.ZERO,
    tag: String? = null
): Flow<T> = replayShareImpl(scope = scope, defaultValue = defaultValue, timeout = timeout, tag = tag)

private fun <T> Flow<T>.replayShareImpl(
    scope: CoroutineScope,
    defaultValue: Any?,
    timeout: Duration = Duration.ZERO,
    tag: String?
): Flow<T> {
    var lastValue: Any? = defaultValue
    return this
        .onEach { lastValue = it }
        .onCompletion { lastValue = defaultValue }
        .catch { lastValue = defaultValue }
        .shareIn(scope = scope, cacheSize = 0, timeout = timeout, tag = tag)
        .onStart { if (lastValue !== Null) emit(lastValue as T) }
}

internal object Null
