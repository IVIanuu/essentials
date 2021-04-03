package com.ivianuu.essentials.coroutines

import kotlinx.coroutines.CompletionHandler
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.CoroutineStart
import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.channels.ReceiveChannel
import kotlinx.coroutines.channels.SendChannel
import kotlinx.coroutines.launch
import kotlin.coroutines.CoroutineContext
import kotlin.coroutines.EmptyCoroutineContext

interface ActorScope<E> : CoroutineScope, ReceiveChannel<E> {
    val channel: Channel<E>
}

fun <E> CoroutineScope.commonActor(
    context: CoroutineContext = EmptyCoroutineContext,
    capacity: Int = 0,
    start: CoroutineStart = CoroutineStart.DEFAULT,
    onCompletion: CompletionHandler? = null,
    block: suspend ActorScope<E>.() -> Unit
): SendChannel<E> {
    val channel = Channel<E>(capacity)
    val job = launch(context, start) {
        block(
            object : ActorScope<E>, CoroutineScope by this@commonActor, ReceiveChannel<E> by channel {
                override val channel: Channel<E>
                    get() = channel
            }
        )
    }
    if (onCompletion != null) job.invokeOnCompletion(handler = onCompletion)
    return object : SendChannel<E> by channel {
        override suspend fun send(element: E) {
            job.start()
            channel.send(element)
        }

        override fun offer(element: E): Boolean {
            job.start()
            return channel.offer(element)
        }

        override fun close(cause: Throwable?): Boolean {
            val closed = channel.close(cause)
            job.start()
            return closed
        }
    }
}
