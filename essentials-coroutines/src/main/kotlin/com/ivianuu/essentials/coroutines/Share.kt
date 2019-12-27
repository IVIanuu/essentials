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
import kotlinx.coroutines.Job
import kotlinx.coroutines.channels.BroadcastChannel
import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.FlowCollector
import kotlinx.coroutines.flow.asFlow
import kotlinx.coroutines.flow.collect
import kotlinx.coroutines.flow.emitAll
import kotlinx.coroutines.flow.onCompletion
import kotlinx.coroutines.flow.onEach
import kotlinx.coroutines.flow.onStart
import kotlinx.coroutines.launch
import kotlin.time.Duration

fun <T> Flow<T>.cache(cacheSize: Int): Flow<T> {
    require(cacheSize > 0) { "cacheSize parameter must be greater than 0, but was $cacheSize" }

    val cache = CircularArray<T>(cacheSize)

    return onEach { value ->
        // While flowing, also record all values in the cache.
        cache.add(value)
    }.onStart {
        // Before emitting any values in sourceFlow,
        // emit any cached values starting with the oldest.
        cache.forEach { emit(it) }
    }
}

fun <T> Flow<T>.shareIn(
    scope: CoroutineScope,
    cacheSize: Int = 0,
    timeout: Duration = Duration.ZERO
): Flow<T> = SharedFlow(this, scope, cacheSize, timeout)

private class SharedFlow<T>(
    private val sourceFlow: Flow<T>,
    private val scope: CoroutineScope,
    private val cacheSize: Int,
    private val timeout: Duration
) : Flow<T> {

    private val lock = this
    private var cache = CircularArray<T>(cacheSize)

    private var collectingJob: Job? = null
    private var resetJob: Job? = null
    private var refCount = 0
    private var channel = BroadcastChannel<T>(Channel.BUFFERED)

    init {
        require(cacheSize >= 0) { "cacheSize parameter must be at least 0, but was $cacheSize" }
    }

    override suspend fun collect(
        collector: FlowCollector<T>
    ) {
        collector.emitAll(
            channel
                .asFlow()
                .onStart { onEnter() }
                .onCompletion { onExit() }
                .replayIfNeeded()
        )
    }

    private fun onEnter() {
        synchronized(lock) {
            refCount++

            cancelPendingReset()

            if (collectingJob == null) {
                collectingJob = scope.launch {
                    sourceFlow
                        .cacheIfNeeded()
                        .collect { channel.send(it) }
                }
            }
        }
    }

    private fun onExit() {
        synchronized(lock) {
            refCount--
            if (refCount == 0) {
                dispatchDelayedResetOrReset()
            }
        }
    }

    private fun dispatchDelayedResetOrReset() {
        if (timeout == Duration.ZERO) {
            synchronized(lock) { reset() }
            return
        }
        synchronized(lock) {
            resetJob?.cancel()
            resetJob = scope.launch {
                delay(timeout.toLongMilliseconds())
                synchronized(lock) { reset() }
            }
        }
    }

    private fun cancelPendingReset() {
        resetJob?.cancel()
        resetJob = null
    }

    private fun reset() {
        cancelPendingReset()
        collectingJob?.cancel()
        collectingJob = null
        cache = CircularArray(cacheSize)
        channel = BroadcastChannel(Channel.BUFFERED)
    }

    private fun Flow<T>.replayIfNeeded(): Flow<T> = if (cacheSize > 0) {
        onStart { cache.forEach { emit(it) } }
    } else this

    private fun Flow<T>.cacheIfNeeded(): Flow<T> = if (cacheSize > 0) {
        onEach { value -> cache.add(value) }
    } else this

}