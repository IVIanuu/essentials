package com.ivianuu.essentials.coroutines

import kotlinx.coroutines.CompletableDeferred
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.CoroutineStart
import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.launch

interface Actor<T> {
    suspend fun act(message: T)
    fun tryAct(message: T): Boolean
}

fun CoroutineScope.actor(capacity: Int = 0): Actor<suspend () -> Unit> = actor(capacity) {
    it()
}

fun <T> CoroutineScope.actor(
    capacity: Int = 0,
    onMessage: suspend (T) -> Unit
): Actor<T> = ActorImpl(this, capacity, onMessage)

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

private class ActorImpl<T>(
    scope: CoroutineScope,
    capacity: Int,
    onMessage: suspend (T) -> Unit
) : Actor<T> {
    private val mailbox = Channel<T>(capacity = capacity)
    private val job = scope.launch(start = CoroutineStart.LAZY) {
        for (message in mailbox)
            onMessage(message)
    }

    override suspend fun act(message: T) {
        job.start()
        mailbox.send(message)
    }

    override fun tryAct(message: T): Boolean {
        job.start()
        return mailbox.offer(message)
    }
}
