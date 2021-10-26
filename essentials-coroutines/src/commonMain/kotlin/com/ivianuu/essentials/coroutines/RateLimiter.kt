/*
 * Copyright 2021 Manuel Wrage
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package com.ivianuu.essentials.coroutines

import com.ivianuu.essentials.time.Clock
import com.ivianuu.injekt.Inject
import kotlinx.coroutines.delay
import kotlinx.coroutines.sync.Mutex
import kotlinx.coroutines.sync.withLock
import kotlin.jvm.Volatile
import kotlin.time.Duration

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
  private val mutex = Mutex()
  private val permitDuration = interval / eventsPerInterval

  @Volatile private var cursor = clock()

  override suspend fun acquire() {
    val now = clock()

    val wakeUpTime = mutex.withLock {
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

    val wakeUpTime = mutex.withLock {
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
