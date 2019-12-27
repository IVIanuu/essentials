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

import androidx.collection.CircularArray
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.channels.BroadcastChannel
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.FlowCollector
import kotlinx.coroutines.flow.asFlow
import kotlinx.coroutines.flow.broadcastIn
import kotlinx.coroutines.flow.emitAll
import kotlinx.coroutines.flow.onCompletion
import kotlinx.coroutines.flow.onEach
import kotlinx.coroutines.flow.onStart
import kotlinx.coroutines.sync.Mutex
import kotlinx.coroutines.sync.withLock

fun <T> Flow<T>.cache(history: Int): Flow<T> = asCachedFlow(history)

fun <T> Flow<T>.shareIn(
    scope: CoroutineScope,
    cacheHistory: Int = 0
): Flow<T> = asSharedFlow(scope, cacheHistory)

internal fun <T> Flow<T>.asCachedFlow(cacheHistory: Int): Flow<T> {
    require(cacheHistory > 0) { "cacheHistory parameter must be greater than 0, but was $cacheHistory" }

    val cache = CircularArray<T>(cacheHistory)

    return onEach { value ->
        // While flowing, also record all values in the cache.
        cache.addLast(value)
    }.onStart {
        // Before emitting any values in sourceFlow,
        // emit any cached values starting with the oldest.
        (0 until cache.size())
            .map { cache[it] }
            .forEach { emit(it) }
    }
}

internal fun <T> Flow<T>.asSharedFlow(
    scope: CoroutineScope,
    cacheHistory: Int
): Flow<T> = SharedFlow(this, scope, cacheHistory)

internal class SharedFlow<T>(
    private val sourceFlow: Flow<T>,
    private val scope: CoroutineScope,
    private val cacheHistory: Int
) : Flow<T> {

    private var refCount = 0
    private var cache = CircularArray<T>(cacheHistory)
    private val mutex = Mutex()

    init {
        require(cacheHistory >= 0) { "cacheHistory parameter must be at least 0, but was $cacheHistory" }
    }

    override suspend fun collect(
        collector: FlowCollector<T>
    ) = collector.emitAll(createFlow())

    // Replay happens per new collector, if cacheHistory > 0.
    private suspend fun createFlow(): Flow<T> = getChannel()
        .asFlow()
        .replayIfNeeded()
        .onCompletion { onCollectEnd() }

    // lazy holder for the BroadcastChannel, which is reset whenever all collection ends
    private var lazyChannelRef = createLazyChannel()

    // must be lazy so that the broadcast doesn't begin immediately after a reset
    private fun createLazyChannel() = lazy(LazyThreadSafetyMode.NONE) {
        sourceFlow.cacheIfNeeded()
            .broadcastIn(scope)
    }

    private fun Flow<T>.replayIfNeeded(): Flow<T> = if (cacheHistory > 0) {
        onStart {
            (0 until cache.size())
                .map { cache[it] }
                .forEach { emit(it) }
        }
    } else this

    private fun Flow<T>.cacheIfNeeded(): Flow<T> = if (cacheHistory > 0) {
        onEach { value ->
            // While flowing, also record all values in the cache.
            cache.addLast(value)
        }
    } else this

    private fun reset() {
        cache = CircularArray(cacheHistory)
        lazyChannelRef = createLazyChannel()
    }

    private suspend fun onCollectEnd() = mutex.withLock {
        if (--refCount == 0) reset()
    }

    private suspend fun getChannel(): BroadcastChannel<T> = mutex.withLock {
        refCount++
        lazyChannelRef.value
    }

}