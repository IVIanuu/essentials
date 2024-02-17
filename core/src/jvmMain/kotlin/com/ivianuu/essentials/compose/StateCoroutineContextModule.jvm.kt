package com.ivianuu.essentials.compose

import androidx.compose.runtime.*
import com.ivianuu.injekt.*
import kotlinx.coroutines.*

@Provide actual object StateCoroutineContextModule {
  @Provide actual val context: StateCoroutineContext by lazy {
    Dispatchers.Main + ImmediateFrameClock
  }
}

private object ImmediateFrameClock : MonotonicFrameClock {
  override suspend fun <R> withFrameNanos(onFrame: (frameTimeNanos: Long) -> R): R =
    onFrame(0L)
}
