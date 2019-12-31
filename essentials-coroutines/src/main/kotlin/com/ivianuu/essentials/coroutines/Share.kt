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

import kotlinx.coroutines.CompletableDeferred
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Job
import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.channels.ClosedSendChannelException
import kotlinx.coroutines.channels.actor
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.FlowCollector
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.collect
import kotlinx.coroutines.flow.consumeAsFlow
import kotlinx.coroutines.flow.emitAll
import kotlinx.coroutines.flow.onCompletion
import kotlinx.coroutines.flow.onEach
import kotlinx.coroutines.flow.onStart
import kotlinx.coroutines.flow.transform
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
) : Flow<T> {

    private var cache = CircularArray<T>(cacheSize)

    private var collecting = false
    private var collectionJob: Job? = null
    private var resetJob: Job? = null
    private val channels = mutableListOf<Channel<Message.Dispatch.Value<T>>>()

    private val actorChannel = scope.actor<Message<T>>(
        capacity = Channel.UNLIMITED
    ) {
        for (msg in channel) {
            when (msg) {
                is Message.AddChannel -> {
                    channels += msg.channel
                    println("SharedFlows: $tag -> added channel ${msg.channel} ${channels.size}")
                    cancelPendingReset()
                    startCollectingIfNeeded()
                }
                is Message.RemoveChannel -> {
                    channels -= msg.channel
                    println("SharedFlows: $tag -> removed channel ${msg.channel} ${channels.size}")
                    dispatchDelayedResetOrResetIfNeeded()
                }
                is Message.Dispatch.Value -> {
                    println("SharedFlows: $tag -> dispatch value -> ${msg.value}")
                    channels.forEach { it.send(msg) }
                }
                is Message.Dispatch.Error -> {
                    println("SharedFlows: $tag -> dispatch error -> ${msg.error}")
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

    override suspend fun collect(
        collector: FlowCollector<T>
    ) {
        val channel = Channel<Message.Dispatch.Value<T>>(Channel.UNLIMITED)
        collector.emitAll(
            channel.consumeAsFlow()
                .onStart {
                    println("SharedFlows: $tag -> child start $channel")
                    actorChannel.send(Message.AddChannel(channel))
                }
                .transform {
                    println("SharedFlows: $tag -> child value ${it.value} $channel")
                    emit(it.value)
                    it.delivered.complete(Unit)
                }
                .onCompletion {
                    println("SharedFlows: $tag -> child complete $channel")
                    actorChannel.send(Message.RemoveChannel(channel))
                }
                .replayIfNeeded()
        )
    }

    private fun startCollectingIfNeeded() {
        if (collecting) return
        collecting = true

        scope.launch {
            try {
                collectionJob = scope.launch {
                    try {
                        sourceFlow
                            .catch {
                                actorChannel.send(Message.Dispatch.Error(it))
                            }
                            .cacheIfNeeded()
                            .collect {
                                val ack = CompletableDeferred<Unit>()
                                actorChannel.send(Message.Dispatch.Value(it, ack))
                                ack.await()
                            }
                    } catch (closed: ClosedSendChannelException) {
                    }
                }

                collectionJob?.join()
            } finally {
                try {
                    actorChannel.send(Message.Dispatch.UpstreamFinished())
                } catch (closed: ClosedSendChannelException) {
                }
            }
        }
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
        resetJob?.cancel()
        resetJob = null
    }

    private fun reset() {
        cancelPendingReset()
        collecting = false
        collectionJob?.cancel()
        collectionJob = null
        channels.forEach { it.close() }
        channels.clear()
        cache = CircularArray(cacheSize)
    }

    private fun Flow<T>.replayIfNeeded(): Flow<T> = if (cacheSize > 0) {
        onStart { cache.forEach { emit(it) } }
    } else this

    private fun Flow<T>.cacheIfNeeded(): Flow<T> = if (cacheSize > 0) {
        onEach { value -> cache.add(value) }
    } else this

    private sealed class Message<T> {
        class AddChannel<T>(val channel: Channel<Dispatch.Value<T>>) : Message<T>()
        class RemoveChannel<T>(val channel: Channel<Dispatch.Value<T>>) : Message<T>()
        sealed class Dispatch<T> : Message<T>() {
            class Value<T>(
                val value: T,
                val delivered: CompletableDeferred<Unit>
            ) : Dispatch<T>()
            class Error<T>(val error: Throwable) : Dispatch<T>()
            class UpstreamFinished<T> : Dispatch<T>()
        }
        class Reset<T> : Message<T>()
    }

}