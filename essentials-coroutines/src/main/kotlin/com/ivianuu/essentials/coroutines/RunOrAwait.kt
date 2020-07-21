package com.ivianuu.essentials.coroutines

import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Deferred
import kotlinx.coroutines.async
import kotlinx.coroutines.coroutineScope
import kotlinx.coroutines.launch
import java.util.concurrent.ConcurrentHashMap
import java.util.concurrent.atomic.AtomicBoolean

suspend fun <T> runOrAwait(
    key: Any,
    block: suspend CoroutineScope.() -> T
): T = coroutineScope {
    val deferred = deferreds[key]?.takeIf { it.isActive }
        ?: async(block = block).also { deferreds[key] = it }

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

private val deferreds = ConcurrentHashMap<Any, Deferred<*>>()
private val deferredsCleanLaunched = AtomicBoolean()
