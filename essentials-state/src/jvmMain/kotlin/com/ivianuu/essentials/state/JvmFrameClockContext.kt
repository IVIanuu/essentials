/*
 * Copyright 2021 Manuel Wrage. Use of this source code is governed by the Apache 2.0 license.
 */

package com.ivianuu.essentials.state

import androidx.compose.runtime.MonotonicFrameClock
import kotlin.coroutines.CoroutineContext

actual fun defaultFrameClockContext(): CoroutineContext = ImmediateFrameClock

object ImmediateFrameClock : MonotonicFrameClock {
  override suspend fun <R> withFrameNanos(onFrame: (frameTimeNanos: Long) -> R): R =
    onFrame(System.nanoTime())
}
