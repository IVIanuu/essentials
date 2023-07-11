/*
 * Copyright 2022 Manuel Wrage. Use of this source code is governed by the Apache 2.0 license.
 */

package com.ivianuu.essentials.coroutines

import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.isActive
import kotlin.coroutines.coroutineContext
import kotlin.time.Duration

fun timerFlow(duration: Duration): Flow<Long> = flow {
  var value = 0L
  while (coroutineContext.isActive) {
    emit(value++)
    delay(duration)
  }
}
