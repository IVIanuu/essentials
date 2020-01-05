package com.ivianuu.essentials.coroutines

import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Job
import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.channels.ReceiveChannel
import kotlinx.coroutines.channels.SendChannel
import kotlinx.coroutines.channels.ValueOrClosed
import kotlinx.coroutines.flow.AbstractFlow
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.FlowCollector
import kotlinx.coroutines.flow.consumeAsFlow
import kotlinx.coroutines.flow.emitAll
import kotlinx.coroutines.flow.onCompletion
import kotlinx.coroutines.flow.onEach
import kotlinx.coroutines.flow.onStart
import kotlinx.coroutines.flow.produceIn
import kotlinx.coroutines.isActive
import kotlinx.coroutines.launch
import kotlinx.coroutines.selects.select
import kotlinx.coroutines.sync.Mutex
import kotlinx.coroutines.sync.withLock
import kotlin.time.Duration
import kotlin.time.milliseconds

fun <T> Flow<T>.shareIn(
    scope: CoroutineScope,
    cacheSize: Int = 0,
    timeout: Duration = Duration.ZERO,
    tag: String? = null
): Flow<T> = SharedFlow2(scope, this, cacheSize, timeout, tag)

private class SharedFlow2<T>(
    private val scope: CoroutineScope,
    private val source: Flow<T>,
    private val cacheSize: Int,
    private val timeout: Duration,
    private val tag: String?
) : AbstractFlow<T>() {
    private val mutex = Mutex()
    private val collectors = mutableListOf<SendChannel<T>>()

    private var cache = CircularArray<T>(cacheSize)
    private var nextDebounceTarget: Duration? = null

    private var actor = Channel<Action<T>>(Channel.UNLIMITED)
    private var sourceChannel: ReceiveChannel<T>? = null

    private var multicastActorJob: Job? = null

    override suspend fun collectSafely(collector: FlowCollector<T>) {
        val channel = Channel<T>(Channel.UNLIMITED)
        try {
            ensureActorActive()
            println("SharedFlows: $tag -> shared add collector")
            actor.send(Action.Add(channel))
            collector.emitAll(
                channel.consumeAsFlow()
                    .onStart {
                        println("SharedFlows: $tag -> on shared start")
                    }
                    .onEach {
                        println("SharedFlows: $tag -> on shared value $it")
                    }
                    .onCompletion {
                        println("SharedFlows: $tag -> on shared completion $it")
                    }
            )
        } finally {
            println("SharedFlows: $tag -> shared remove collector")
            actor.send(Action.Remove(channel))
        }
    }

    private suspend fun ensureActorActive() {
        if (multicastActorJob?.isActive != true) {
            mutex.withLock {
                if (multicastActorJob?.isActive != true) {
                    startFlowActor()
                }
            }
        }
    }

    private fun startFlowActor() {
        println("SharedFlows: $tag -> start actor")
        actor = Channel(Channel.UNLIMITED)
        multicastActorJob = scope.launch {
            while (isActive) {
                val currentSourceChannel = sourceChannel

                select<Unit> {
                    actor.onReceive { action ->
                        onActorAction(action)
                    }

                    if (currentSourceChannel != null) {
                        currentSourceChannel.onReceiveOrClosed { valueOrClosed ->
                            onSourceFlowData(valueOrClosed)
                        }
                    }

                    if (nextDebounceTarget != null) {
                        onTimeout(nextDebounceTarget!!.toLongMilliseconds() - System.currentTimeMillis()) {
                            println("SharedFlows: $tag -> close source from timeout")
                            closeSource()
                            nextDebounceTarget = null
                        }
                    }
                }
            }
        }
    }

    private suspend fun onActorAction(action: Action<T>) {
        when (action) {
            is Action.Add -> {
                println("SharedFlows: $tag -> add channel ${action.channel}")

                collectors.add(action.channel)

                if (sourceChannel == null) {
                    println("SharedFlows: $tag -> start source flow")
                    sourceChannel = source.produceIn(scope)
                }

                cache.forEach { action.channel.send(it) }

                nextDebounceTarget = null
            }
            is Action.Remove -> {
                println("SharedFlows: $tag -> remove channel ${action.channel}")

                val collectorIndex = collectors.indexOf(action.channel)

                if (collectorIndex >= 0) {
                    val removedCollector = collectors.removeAt(collectorIndex)
                    removedCollector.close()
                }

                if (collectors.isEmpty()) {
                    if (timeout != Duration.ZERO) {
                        nextDebounceTarget = System.currentTimeMillis().milliseconds + timeout
                    } else {
                        closeSource()
                    }
                }
            }
        }
    }

    private fun closeSource() {
        println("SharedFlows: $tag -> close source")
        cache = CircularArray(cacheSize)
        sourceChannel?.cancel()
        sourceChannel = null
        multicastActorJob?.cancel()
    }

    private suspend fun onSourceFlowData(valueOrClosed: ValueOrClosed<T>) {
        if (valueOrClosed.isClosed) {
            println("SharedFlows: $tag -> source completed")
            collectors.forEach { it.close(valueOrClosed.closeCause) }
            collectors.clear()
            closeSource()
        } else {
            println("SharedFlows: $tag -> emit source value ${valueOrClosed.value} to $collectors")
            collectors.forEach {
                try {
                    if (cacheSize > 0) cache.add(valueOrClosed.value)
                    it.send(valueOrClosed.value)
                } catch (e: Exception) {
                    e.printStackTrace()
                    // Ignore downstream exceptions
                }
            }
        }
    }

    private sealed class Action<T> {
        class Add<T>(val channel: SendChannel<T>) : Action<T>()
        class Remove<T>(val channel: SendChannel<T>) : Action<T>()
    }
}