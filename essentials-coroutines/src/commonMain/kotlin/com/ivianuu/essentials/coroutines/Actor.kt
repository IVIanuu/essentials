package com.ivianuu.essentials.coroutines

import kotlinx.coroutines.CompletableDeferred
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.CoroutineStart
import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.launch

interface Actor {
    suspend fun act(task: suspend CoroutineScope.() -> Unit)
    fun tryAct(task: suspend CoroutineScope.() -> Unit): Boolean
}

fun Actor(
    scope: CoroutineScope,
    capacity: Int = 0,
    init: (suspend CoroutineScope.() -> Unit)? = null,
    start: CoroutineStart = CoroutineStart.DEFAULT,
): Actor = ActorImpl(scope, capacity, start, init)

suspend fun <T> Actor.actAndReply(block: suspend CoroutineScope.() -> T): T {
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

private class ActorImpl(
    scope: CoroutineScope,
    capacity: Int,
    start: CoroutineStart = CoroutineStart.DEFAULT,
    private var init: (suspend CoroutineScope.() -> Unit)? = null
) : Actor {
    private val tasks = Channel<suspend CoroutineScope.() -> Unit>(capacity = capacity)

    private val taskJob = scope.launch(start = start) {
        init?.invoke(this)
        init = null
        for (task in tasks) task()
    }

    override suspend fun act(task: suspend CoroutineScope.() -> Unit) {
        taskJob.start()
        tasks.send(task)
    }

    override fun tryAct(task: suspend CoroutineScope.() -> Unit): Boolean {
        taskJob.start()
        return tasks.offer(task)
    }
}
