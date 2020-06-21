package com.ivianuu.essentials.coroutines

import kotlinx.coroutines.delay
import kotlin.time.Duration
import kotlin.time.milliseconds
import kotlin.time.seconds

suspend fun <T> retry(
    times: Int = Int.MAX_VALUE,
    initialDelay: Duration = 100.milliseconds,
    maxDelay: Duration = 1.seconds,
    factor: Double = 2.0,
    predicate: suspend (Throwable) -> Boolean = { true },
    block: suspend () -> T
): T {
    var currentDelay = initialDelay
    repeat(times - 1) {
        try {
            return block()
        } catch (t: Throwable) {
            if (!predicate(t)) throw RuntimeException(t)
        }
        delay(currentDelay)
        currentDelay = (currentDelay * factor).coerceAtMost(maxDelay)
    }

    return block()
}
