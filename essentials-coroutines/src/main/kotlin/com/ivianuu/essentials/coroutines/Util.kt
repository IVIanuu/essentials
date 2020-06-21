package com.ivianuu.essentials.coroutines

import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Deferred
import kotlinx.coroutines.Job
import kotlinx.coroutines.async
import kotlinx.coroutines.coroutineScope
import kotlinx.coroutines.launch
import java.util.concurrent.ConcurrentHashMap
import java.util.concurrent.atomic.AtomicBoolean

@PublishedApi
internal val deferreds = ConcurrentHashMap<Any, Deferred<*>>()
@PublishedApi
internal val deferredsCleanLaunched = AtomicBoolean()

suspend inline fun <T> asyncOrAwait(
    key: Any,
    crossinline action: suspend CoroutineScope.() -> T
): T = coroutineScope {
    val deferred = deferreds[key]?.takeIf { it.isActive }
        ?: async { action() }.also { deferreds[key] = it }

    if (deferreds.size > 100 && !deferredsCleanLaunched.getAndSet(true)) {
        launch {
            // Remove any complete entries
            deferreds.entries.removeAll { it.value.isCompleted }
            deferredsCleanLaunched.set(false)
        }
    }

    @Suppress("UNCHECKED_CAST")
    deferred.await() as T
}

val jobs = ConcurrentHashMap<Any, Job>()
val jobsCleanLaunched = AtomicBoolean()

suspend inline fun launchOrJoin(
    key: Any,
    crossinline action: suspend CoroutineScope.() -> Unit
) = coroutineScope {
    val job = jobs[key]?.takeIf { it.isActive }
        ?: launch { action() }
            .also { jobs[key] = it }

    if (jobs.size > 100 && !jobsCleanLaunched.getAndSet(true)) {
        launch {
            // Remove any complete entries
            jobs.entries.removeAll { it.value.isCompleted }
            jobsCleanLaunched.set(false)
        }
    }

    job.join()
}

