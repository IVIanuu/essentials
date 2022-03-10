/*
 * Copyright 2022 Manuel Wrage. Use of this source code is governed by the Apache 2.0 license.
 */

package com.ivianuu.essentials.coroutines

import com.ivianuu.essentials.time.*
import com.ivianuu.injekt.*
import kotlinx.coroutines.*
import kotlinx.coroutines.sync.*
import kotlin.time.*

interface RateLimiter {
  suspend fun acquire()

  suspend fun tryAcquire(): Boolean
}

fun RateLimiter(
  eventsPerInterval: Int,
  interval: Duration,
  @Inject clock: Clock
): RateLimiter = RateLimiterImpl(eventsPerInterval, interval)

internal class RateLimiterImpl(
  eventsPerInterval: Int,
  interval: Duration,
  @Inject private val clock: Clock
) : RateLimiter {
  private val lock = Mutex()
  private val permitDuration = interval / eventsPerInterval

  /* // todo @Volatile */ private var cursor = clock()

  override suspend fun acquire() {
    val now = clock()

    val wakeUpTime = lock.withLock {
      val base = if (cursor > now) cursor else now
      cursor = base + permitDuration
      base
    }

    delay(wakeUpTime - now)
  }

  override suspend fun tryAcquire(): Boolean {
    val now = clock()

    if (cursor > now)
      return false

    val wakeUpTime = lock.withLock {
      val base = if (cursor > now) cursor else now
      if (base > now)
        return false
      cursor = base + permitDuration
      base
    }

    delay(wakeUpTime - now)

    return true
  }
}
