package com.ivianuu.essentials.coroutines

import kotlinx.coroutines.CompletableDeferred
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.CoroutineStart
import kotlinx.coroutines.Job
import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.launch

interface ActorTraits {
    val job: Job
    fun close()
    fun cancel()
    suspend fun join()
}

interface Actor : ActorTraits {
    suspend fun act(block: suspend () -> Unit)
    fun tryAct(block: suspend () -> Unit): Boolean
}

interface TypedActor<E> : ActorTraits {
    suspend fun send(message: E)
    fun offer(message: E): Boolean
}

fun CoroutineScope.actor(
    capacity: Int = 0,
    start: CoroutineStart = CoroutineStart.LAZY
): Actor = object : AbstractActor(this, capacity, start) {
}

suspend fun <T> Actor.actAndReply(block: suspend () -> T): T {
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

fun <E> CoroutineScope.typedActor(
    capacity: Int = 0,
    start: CoroutineStart = CoroutineStart.LAZY,
    onMessage: suspend (E) -> Unit
): TypedActor<E> = object : AbstractTypedActor<E>(this, capacity, start) {
    override suspend fun onMessage(message: E) {
        onMessage(message)
    }
}

abstract class BaseActor<E>(
    scope: CoroutineScope,
    capacity: Int = 0,
    start: CoroutineStart = CoroutineStart.LAZY
) : ActorTraits {
    private val mailbox = Channel<E>(capacity = capacity)
    override val job = scope.launch(start = start) {
        try {
            onStart()
            for (message in mailbox)
                onMessage(message)
        } catch (e: Throwable) {
            onClose()
        }
    }

    override fun close() {
        mailbox.close()
    }

    override fun cancel() {
        job.cancel()
        mailbox.cancel()
    }

    override suspend fun join() = job.join()

    protected open suspend fun onStart() {
    }

    protected open fun onClose() {
    }

    protected abstract suspend fun onMessage(message: E)

    protected suspend fun sendMessage(message: E) {
        job.start()
        mailbox.send(message)
    }

    protected fun offerMessage(message: E): Boolean {
        job.start()
        return mailbox.offer(message)
    }
}

abstract class AbstractActor(
    scope: CoroutineScope,
    capacity: Int = 0,
    start: CoroutineStart = CoroutineStart.LAZY
) : BaseActor<suspend () -> Unit>(scope, capacity, start), Actor {
    override suspend fun act(block: suspend () -> Unit) = sendMessage(block)
    override fun tryAct(block: suspend () -> Unit): Boolean = offerMessage(block)
    override suspend fun onMessage(message: suspend () -> Unit) {
        message()
    }
}

abstract class AbstractTypedActor<E>(
    scope: CoroutineScope,
    capacity: Int = 0,
    start: CoroutineStart = CoroutineStart.LAZY
) : BaseActor<E>(scope, capacity, start), TypedActor<E> {
    override suspend fun send(message: E) = sendMessage(message)
    override fun offer(message: E): Boolean = offerMessage(message)
}
