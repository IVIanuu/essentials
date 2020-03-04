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
import kotlinx.coroutines.flow.onCompletion
import kotlinx.coroutines.flow.onEach
import kotlinx.coroutines.flow.onStart
import kotlinx.coroutines.sync.Mutex
import kotlinx.coroutines.sync.withLock
import kotlin.time.Duration

fun <T> Flow<T>.replayShareIn(
    scope: CoroutineScope,
    timeout: Duration = Duration.ZERO,
    tag: String? = null
): Flow<T> = ReplayShareFlow(upstream = this, scope = scope, defaultValue = Null, timeout = timeout, tag = tag)

fun <T> Flow<T>.replayShareIn(
    scope: CoroutineScope,
    defaultValue: T,
    timeout: Duration = Duration.ZERO,
    tag: String? = null
): Flow<T> = ReplayShareFlow(upstream = this, scope = scope, defaultValue = defaultValue, timeout = timeout, tag = tag)

private class ReplayShareFlow<T>(
    upstream: Flow<T>,
    scope: CoroutineScope,
    defaultValue: Any?,
    timeout: Duration = Duration.ZERO,
    private val tag: String?
) : AbstractFlow<T>() {

    private var lastValue = defaultValue
    private val mutex = Mutex()

    private val sharedFlow = upstream
        .onEach { item ->
            println("ReplayShare: $tag -> source cache emission $item")
            mutex.withLock { lastValue = item }
        }
        .onCompletion {
            println("ReplayShare: $tag -> source completed with cause $it")
        }
        .catch {
            mutex.withLock { lastValue = defaultValue }
            println("ReplayShare: $tag -> source error set to default $defaultValue")
            throw it
        }
        .shareIn(scope = scope, cacheSize = 0, timeout = timeout, tag = tag)

    override suspend fun collectSafely(collector: FlowCollector<T>) {
        collector.emitAll(
            sharedFlow
                .onStart {
                    val lastValue = mutex.withLock { lastValue }
                    if (lastValue !== Null) {
                        println("ReplayShare: $tag -> shared emit last value on start $lastValue")
                        emit(lastValue as T)
                    } else {
                        println("ReplayShare: $tag -> shared no last value skip")
                    }
                }
                .onStart {
                    println("ReplayShare: $tag -> shared on start")
                }
                .onEach {
                    println("ReplayShare: $tag -> shared on value $it")
                }
                .onCompletion {
                    println("ReplayShare: $tag -> shared on complete $it")
                }
        )
    }
}

internal object Null
