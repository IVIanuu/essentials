package com.ivianuu.essentials.coroutines

import kotlinx.coroutines.async
import kotlinx.coroutines.awaitAll
import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.supervisorScope

private val defaultConcurrency by lazy(LazyThreadSafetyMode.NONE) {
    Runtime.getRuntime().availableProcessors().coerceAtLeast(3)
}

suspend fun <A, B> Collection<A>.parallelMap(
    concurrency: Int = defaultConcurrency,
    block: suspend (A) -> B
): List<B> = supervisorScope {
    val semaphore = Channel<Unit>(concurrency)
    map { item ->
        async {
            semaphore.send(Unit) // Acquire concurrency permit
            try {
                block(item)
            } finally {
                semaphore.receive() // Release concurrency permit
            }
        }
    }.awaitAll()
}

suspend fun <A> Collection<A>.parallelForEach(
    concurrency: Int = defaultConcurrency,
    block: suspend (A) -> Unit
) {
    parallelMap(concurrency) { block(it) }
}
