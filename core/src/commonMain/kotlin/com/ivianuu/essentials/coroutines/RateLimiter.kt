/*
 * Copyright 2022 Manuel Wrage. Use of this source code is governed by the Apache 2.0 license.
 */

package com.ivianuu.essentials.coroutines

import com.ivianuu.essentials.time.Clock
import com.ivianuu.injekt.Inject
import kotlinx.coroutines.delay
import kotlinx.coroutines.sync.Mutex
import kotlinx.coroutines.sync.withLock
import kotlin.time.Duration

class RateLimiter(
  private val eventsPerInterval: Int,
  private val interval: Duration,
  @Inject private val clock: Clock,
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
