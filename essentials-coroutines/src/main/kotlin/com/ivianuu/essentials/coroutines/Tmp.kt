package com.ivianuu.essentials.coroutines

import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.CoroutineStart
import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.channels.ProducerScope
import kotlinx.coroutines.channels.ReceiveChannel
import kotlinx.coroutines.channels.produce
import kotlinx.coroutines.coroutineScope
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.FlowCollector
import kotlinx.coroutines.flow.channelFlow
import kotlinx.coroutines.flow.collect
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.newCoroutineContext
import kotlinx.coroutines.selects.SelectBuilder
import kotlinx.coroutines.selects.select
import kotlinx.coroutines.sync.Mutex
import kotlin.coroutines.CoroutineContext
import kotlin.coroutines.EmptyCoroutineContext

// todo ir
suspend fun <T> Mutex.withLockNoInline(owner: Any? = null, action: () -> T): T {
    lock(owner)
    try {
        return action()
    } finally {
        unlock(owner)
    }
}

// todo ir
fun <T> callbackFlowNoInline(@BuilderInference block: suspend (ProducerScope<T>) -> Unit): Flow<T> =
    channelFlow(block)

// todo ir
suspend fun <T> Flow<T>.collectNoInline(action: suspend (value: T) -> Unit) {
    collect(ActionCollector(action))
}

// todo ir
private class ActionCollector<T>(private val action: suspend (value: T) -> Unit) : FlowCollector<T> {
    override suspend fun emit(value: T) {
        action(value)
    }
}

// todo ir
fun <T, R> combineNoInline(
    vararg flows: Flow<T>,
    transform: suspend (Array<T>) -> R
): Flow<R> = flow {
    combineInternal(flows, { arrayOfNulls<Any?>(flows.size) }, { emit(transform(it as Array<T>)) })
}

// todo ir
@PublishedApi
internal suspend fun <R, T> FlowCollector<R>.combineInternal(
    flows: Array<out Flow<T>>,
    arrayFactory: () -> Array<T?>,
    transform: suspend FlowCollector<R>.(Array<T>) -> Unit
): Unit = coroutineScope {
    val size = flows.size
    val channels = Array(size) { asFairChannel(flows[it]) }
    val latestValues = arrayOfNulls<Any?>(size)
    val isClosed = Array(size) { false }
    var nonClosed = size
    var remainingNulls = size
    // See flow.combine(other) for explanation.
    while (nonClosed != 0) {
        select<Unit> {
            for (i in 0 until size) {
                onReceive(isClosed[i], channels[i], { isClosed[i] = true; --nonClosed }) { value ->
                    if (latestValues[i] == null) --remainingNulls
                    latestValues[i] = value
                    if (remainingNulls != 0) return@onReceive
                    val arguments = arrayFactory()
                    for (index in 0 until size) {
                        arguments[index] = NULL.unbox(latestValues[index])
                    }
                    transform(arguments as Array<T>)
                }
            }
        }
    }
}

@JvmField
internal val NULL = Symbol("NULL")

internal class Symbol(val symbol: String) {
    override fun toString(): String = symbol

    @Suppress("UNCHECKED_CAST", "NOTHING_TO_INLINE")
    inline fun <T> unbox(value: Any?): T = if (value === this) null as T else value as T
}

// todo ir
private fun CoroutineScope.asFairChannel(flow: Flow<*>): ReceiveChannel<Any> {
    val func: suspend (ProducerScope<Any>) -> Unit = { producerScope ->
        flow.collectNoInline { value -> producerScope.channel.send(value ?: NULL) }
    }
    return produce(block = func)
}


// todo ir
private fun SelectBuilder<Unit>.onReceive(
    isClosed: Boolean,
    channel: ReceiveChannel<Any>,
    onClosed: () -> Unit,
    onReceive: suspend (value: Any) -> Unit
) {
    if (isClosed) return
    @Suppress("DEPRECATION")
    channel.onReceiveOrNull {
        // TODO onReceiveOrClosed when boxing issues are fixed
        if (it === null) onClosed()
        else onReceive(it)
    }
}