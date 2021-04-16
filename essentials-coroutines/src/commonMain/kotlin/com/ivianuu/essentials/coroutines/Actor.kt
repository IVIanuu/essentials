package com.ivianuu.essentials.coroutines

import kotlinx.coroutines.*
import kotlinx.coroutines.channels.*
import kotlin.coroutines.*

interface Actor<T> {
    suspend fun act(message: T)
    fun tryAct(message: T): Boolean
}

fun <T> CoroutineScope.actor(
    coroutineContext: CoroutineContext = this.coroutineContext,
    capacity: Int = 64,
    start: CoroutineStart = CoroutineStart.LAZY,
    block: suspend ActorScope<T>.() -> Unit
): Actor<T> = ActorImpl(coroutineContext, capacity, start, block)

interface ActorScope<T> : CoroutineScope, ReceiveChannel<T>

private class ActorImpl<T>(
    override val coroutineContext: CoroutineContext,
    capacity: Int,
    start: CoroutineStart,
    block: suspend ActorScope<T>.() -> Unit,
    private val mailbox: Channel<T> = Channel(capacity = capacity)
) : Actor<T>, ActorScope<T>, ReceiveChannel<T> by mailbox, CoroutineScope {
    private val job = launch(start = start) { block() }

    override suspend fun act(message: T) {
        job.start()
        mailbox.send(message)
    }

    override fun tryAct(message: T): Boolean {
        job.start()
        return mailbox.offer(message)
    }
}

fun CoroutineScope.actor(
    coroutineContext: CoroutineContext = this.coroutineContext,
    capacity: Int = 64
): Actor<suspend () -> Unit> = actor(coroutineContext, capacity) {
    for (block in this) block()
}

suspend fun <T> Actor<suspend () -> Unit>.actAndReply(block: suspend () -> T): T {
    val reply = CompletableDeferred<T>()
    act {
        try {
            reply.complete(block())
        } catch (e: Throwable) {
            reply.completeExceptionally(e)
        }
    }
    return reply.await()
}
