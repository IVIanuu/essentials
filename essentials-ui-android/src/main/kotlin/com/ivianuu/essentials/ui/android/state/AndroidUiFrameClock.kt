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

package com.ivianuu.essentials.ui.android.state

import android.view.Choreographer
import androidx.compose.runtime.MonotonicFrameClock
import kotlinx.coroutines.suspendCancellableCoroutine
import kotlin.coroutines.ContinuationInterceptor
import kotlin.coroutines.coroutineContext

class AndroidUiFrameClock(private val choreographer: Choreographer) : MonotonicFrameClock {
  override suspend fun <R> withFrameNanos(onFrame: (Long) -> R): R {
    val uiDispatcher = coroutineContext[ContinuationInterceptor] as? AndroidUiDispatcher
    return suspendCancellableCoroutine { co ->
      // Important: this callback won't throw, and AndroidUiDispatcher counts on it.
      val callback = Choreographer.FrameCallback { frameTimeNanos ->
        co.resumeWith(runCatching { onFrame(frameTimeNanos) })
      }

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
