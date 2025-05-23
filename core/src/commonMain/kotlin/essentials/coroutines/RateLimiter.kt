/*
 * Copyright 2022 Manuel Wrage. Use of this source code is governed by the Apache 2.0 license.
 */

package essentials.coroutines

import essentials.*
import injekt.*
import kotlinx.coroutines.*
import kotlinx.coroutines.sync.*
import kotlin.time.*

class RateLimiter(
  private val eventsPerInterval: Int,
  private val interval: Duration,
  private val clock: () -> Now = inject,
) {
  private val lock = Mutex()

  private var remainingEvents = eventsPerInterval
  private var intervalEnd = Duration.ZERO

  suspend fun acquire() = acquireInternal(onLimitExceeded = { null }, onPermit = { })

  suspend fun tryAcquire() = acquireInternal(onLimitExceeded = { false }, onPermit = { true })

  private suspend inline fun <T : Any> acquireInternal(
    onLimitExceeded: () -> T?,
    onPermit: () -> T
  ): T = lock.withLock {
    val now = clock()

    when {
      now >= intervalEnd -> enterNextInterval(now)
      remainingEvents == 0 -> {
        val result = onLimitExceeded()
        if (result != null) return@withLock result
        delay(intervalEnd - now)
        enterNextInterval(clock())
      }
      else -> remainingEvents -= 1
    }

    onPermit()
  }

  private fun enterNextInterval(now: Duration) {
    remainingEvents = eventsPerInterval - 1
    intervalEnd = now + interval
  }
}
