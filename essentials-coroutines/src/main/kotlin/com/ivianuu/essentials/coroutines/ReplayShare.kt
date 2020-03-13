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
import kotlinx.coroutines.flow.AbstractFlow
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.FlowCollector
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.emitAll
import kotlinx.coroutines.flow.onEach
import kotlinx.coroutines.flow.onStart
import kotlinx.coroutines.sync.Mutex
import kotlinx.coroutines.sync.withLock
import kotlin.time.Duration

fun <T> Flow<T>.replayShareIn(
    scope: CoroutineScope,
    timeout: Duration = Duration.ZERO
): Flow<T> = ReplayShareFlow(upstream = this, scope = scope, defaultValue = Null, timeout = timeout)

fun <T> Flow<T>.replayShareIn(
    scope: CoroutineScope,
    defaultValue: T,
    timeout: Duration = Duration.ZERO
): Flow<T> =
    ReplayShareFlow(upstream = this, scope = scope, defaultValue = defaultValue, timeout = timeout)

private class ReplayShareFlow<T>(
    upstream: Flow<T>,
    scope: CoroutineScope,
    defaultValue: Any?,
    timeout: Duration = Duration.ZERO
) : AbstractFlow<T>() {

    private var lastValue = defaultValue
    private val mutex = Mutex()

    private val sharedFlow = upstream
        .onEach { item -> mutex.withLock { lastValue = item } }
        .catch {
            mutex.withLock { lastValue = defaultValue }
            throw it
        }
        .shareIn(scope = scope, cacheSize = 0, timeout = timeout)

    override suspend fun collectSafely(collector: FlowCollector<T>) {
        collector.emitAll(
            sharedFlow
                .onStart {
                    val lastValue = mutex.withLock { lastValue }
                    if (lastValue !== Null) {
                        emit(lastValue as T)
                    }
                }
        )
    }
}

internal object Null
