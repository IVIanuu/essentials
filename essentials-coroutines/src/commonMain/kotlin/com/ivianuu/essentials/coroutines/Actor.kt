package com.ivianuu.essentials.coroutines

import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.CoroutineStart
import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.launch

interface Actor {
    suspend fun act(task: suspend () -> Unit)
    fun tryAct(task: suspend () -> Unit): Boolean
}

fun Actor(
    scope: CoroutineScope,
    capacity: Int = 0,
    start: CoroutineStart = CoroutineStart.DEFAULT,
    init: (suspend () -> Unit)? = null
): Actor = ActorImpl(scope, capacity, start, init)

private class ActorImpl(
    scope: CoroutineScope,
    capacity: Int,
    start: CoroutineStart = CoroutineStart.DEFAULT,
    private var init: (suspend () -> Unit)? = null
) : Actor {
    private val tasks = Channel<suspend () -> Unit>(capacity = capacity)

    private val taskJob = scope.launch(start = start) {
        init?.invoke()
        init = null
        for (task in tasks) task()
    }

    override suspend fun act(task: suspend () -> Unit) {
        taskJob.start()
        tasks.send(task)
    }

    override fun tryAct(task: suspend () -> Unit): Boolean {
        taskJob.start()
        return tasks.offer(task)
    }
}
