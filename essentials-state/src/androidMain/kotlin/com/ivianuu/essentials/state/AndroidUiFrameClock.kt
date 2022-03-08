
/*
 * Copyright 2022 Manuel Wrage. Use of this source code is governed by the Apache 2.0 license.
 */

package com.ivianuu.essentials.state

import android.view.*
import androidx.compose.runtime.*
import kotlinx.coroutines.*
import kotlin.coroutines.*

class AndroidUiFrameClock(private val choreographer: Choreographer) : MonotonicFrameClock {
  override suspend fun <R> withFrameNanos(
    onFrame: (Long) -> R
  ): R {
    val uiDispatcher = coroutineContext[ContinuationInterceptor] as? AndroidUiDispatcher
    return suspendCancellableCoroutine { co ->
      // Important: this callback won't throw, and AndroidUiDispatcher counts on it.
      val callback = Choreographer.FrameCallback { frameTimeNanos ->
        co.resumeWith(runCatching { onFrame(frameTimeNanos) })
      }

      // If we're on an AndroidUiDispatcher then we post callback to happen *after*
      // the greedy trampoline dispatch is complete.
      // This means that onFrame will run on the current choreographer frame if one is
      // already in progress, but withFrameNanos will *not* resume until the frame
      // is complete. This prevents multiple calls to withFrameNanos immediately dispatching
      // on the same frame.

      if (uiDispatcher != null && uiDispatcher.choreographer == choreographer) {
        uiDispatcher.postFrameCallback(callback)
        co.invokeOnCancellation { uiDispatcher.removeFrameCallback(callback) }
      } else {
        choreographer.postFrameCallback(callback)
        co.invokeOnCancellation { choreographer.removeFrameCallback(callback) }
      }
    }
  }
}
