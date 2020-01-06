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

import kotlinx.coroutines.CancellationException
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Job
import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.channels.ClosedSendChannelException
import kotlinx.coroutines.channels.SendChannel
import kotlinx.coroutines.channels.actor
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.AbstractFlow
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.FlowCollector
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.consumeAsFlow
import kotlinx.coroutines.flow.emitAll
import kotlinx.coroutines.flow.launchIn
import kotlinx.coroutines.flow.onCompletion
import kotlinx.coroutines.flow.onEach
import kotlinx.coroutines.flow.onStart
import kotlinx.coroutines.launch
import kotlin.time.Duration

fun <T> Flow<T>.shareIn(
    scope: CoroutineScope,
    cacheSize: Int = 0,
    timeout: Duration = Duration.ZERO,
    tag: String? = null
): Flow<T> = SharedFlow(this, scope, cacheSize, timeout, tag)

private class SharedFlow<T>(
    private val sourceFlow: Flow<T>,
    private val scope: CoroutineScope,
    private val cacheSize: Int,
    private val timeout: Duration,
    private val tag: String?
) : AbstractFlow<T>() {

    private var cache = CircularArray<T>(cacheSize)

    private var collectionJob: Job? = null
    private var resetJob: Job? = null
    private val channels = mutableListOf<SendChannel<T>>()

    private val actorChannel = scope.actor<Message<T>>(
        capacity = Channel.UNLIMITED
    ) {
        for (msg in channel) {
            when (msg) {
                is Message.AddChannel -> {
                    channels += msg.channel
                    println("SharedFlows: $tag -> added channel ${msg.channel} new channels ${channels.size}")
                    cancelPendingReset()
                    startCollectingIfNeeded()
                }
                is Message.RemoveChannel -> {
                    channels -= msg.channel
                    println("SharedFlows: $tag -> removed channel ${msg.channel} new channels $channels")
                    dispatchDelayedResetOrResetIfNeeded()
                }
                is Message.Dispatch.Value -> {
                    val channels = channels.toList()
                    println("SharedFlows: $tag -> dispatch value -> ${msg.value} to $channels")
                    channels.forEach { it.send(msg.value) }
                }
                is Message.Dispatch.Error -> {
                    println("SharedFlows: $tag -> dispatch error -> ${msg.error} to $channels")
                    channels.forEach { it.close(msg.error) }
                }
                is Message.Dispatch.UpstreamFinished -> {
                    println("SharedFlows: $tag -> upstream finished")
                    reset()
                }
                is Message.Reset -> {
                    println("SharedFlows: $tag -> reset")
                    reset()
                }
            }
        }
    }

    init {
        require(cacheSize >= 0) { "cacheSize parameter must be at least 0, but was $cacheSize" }
    }

    override suspend fun collectSafely(collector: FlowCollector<T>) {
        try {
            val channel = Channel<T>(Channel.UNLIMITED)
            collector.emitAll(
                channel.consumeAsFlow()
                    .onStart {
                        println("SharedFlows: $tag -> child on start $channel")
                        actorChannel.send(Message.AddChannel(channel))
                    }
                    .onEach { println("SharedFlows: $tag -> child on each $it $channel") }
                    .onCompletion {
                        println("SharedFlows: $tag -> child on complete $channel")
                        try {
                            actorChannel.send(Message.RemoveChannel(channel))
                        } catch (e: ClosedSendChannelException) {
                        }
                    }
                    .replayIfNeeded()
            )
        } catch (e: CancellationException) {
        }
    }

    private fun startCollectingIfNeeded() {
        if (collectionJob != null) {
            println("SharedFlows: $tag -> already collecting")
            return
        }

        collectionJob = sourceFlow
            .onEach {
                println("SharedFlows: $tag -> source emission $it")
                actorChannel.send(Message.Dispatch.Value(it))
                println("SharedFlows: $tag -> source emission send $it")
            }
            .catch {
                println("SharedFlows: $tag -> catch source error $it")
                actorChannel.send(Message.Dispatch.Error(it))
            }
            .onCompletion {
                println("SharedFlows: $tag -> source completed")
                try {
                    actorChannel.send(Message.Dispatch.UpstreamFinished())
                } catch (e: ClosedSendChannelException) {
                }
            }
            .cacheIfNeeded()
            .launchIn(scope)
    }

    private suspend fun dispatchDelayedResetOrResetIfNeeded() {
        if (channels.isNotEmpty()) return
        println("SharedFlows: $tag -> begin delayed reset $timeout")
        resetJob?.cancel()
        resetJob = scope.launch {
            delay(timeout.toLongMilliseconds())
            actorChannel.send(Message.Reset())
        }
    }

    private fun cancelPendingReset() {
        if (resetJob != null) println("SharedFlows: $tag -> cancel pending reset")
        resetJob?.cancel()
        resetJob = null
    }

    private fun reset() {
        cancelPendingReset()
        collectionJob?.cancel()
        collectionJob = null
        channels.forEach { it.close() }
        channels.clear()
        cache = CircularArray(cacheSize)
    }

    private fun Flow<T>.replayIfNeeded(): Flow<T> = if (cacheSize > 0) {
        onStart {
            println("SharedFlows: $tag -> replay values $cache")
            cache.forEach { emit(it) }
        }

    } else this

    private fun Flow<T>.cacheIfNeeded(): Flow<T> = if (cacheSize > 0) {
        onEach { value ->
            println("SharedFlows: $tag -> cache value $value")
            cache.add(value)
        }
    } else this

    private sealed class Message<T> {
        class AddChannel<T>(val channel: SendChannel<T>) : Message<T>()
        class RemoveChannel<T>(val channel: SendChannel<T>) : Message<T>()
        sealed class Dispatch<T> : Message<T>() {
            class Value<T>(val value: T) : Dispatch<T>()
            class Error<T>(val error: Throwable) : Dispatch<T>()
            class UpstreamFinished<T> : Dispatch<T>()
        }
        class Reset<T> : Message<T>()
    }

}