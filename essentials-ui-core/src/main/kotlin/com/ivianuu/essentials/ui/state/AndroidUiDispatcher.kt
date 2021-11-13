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

package com.ivianuu.essentials.ui.state

import android.os.Looper
import android.view.Choreographer
import androidx.compose.runtime.MonotonicFrameClock
import androidx.core.os.HandlerCompat
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.runBlocking
import kotlin.coroutines.CoroutineContext

class AndroidUiDispatcher private constructor(
  val choreographer: Choreographer,
  private val handler: android.os.Handler
) : CoroutineDispatcher() {

  private val lock = Any()

  private val toRunTrampolined = ArrayDeque<Runnable>()
  private var toRunOnFrame = mutableListOf<Choreographer.FrameCallback>()
  private var spareToRunOnFrame = mutableListOf<Choreographer.FrameCallback>()
  private var scheduledTrampolineDispatch = false
  private var scheduledFrameDispatch = false

  private val dispatchCallback = object : Choreographer.FrameCallback, Runnable {
    override fun run() {
      performTrampolineDispatch()
      synchronized(lock) {
        if (toRunOnFrame.isEmpty()) {
          choreographer.removeFrameCallback(this)
          scheduledFrameDispatch = false
        }
      }
    }

    override fun doFrame(frameTimeNanos: Long) {
      handler.removeCallbacks(this)
      performTrampolineDispatch()
      performFrameDispatch(frameTimeNanos)
    }
  }

  private fun nextTask(): Runnable? = synchronized(lock) {
    toRunTrampolined.removeFirstOrNull()
  }

  private fun performTrampolineDispatch() {
    do {
      var task = nextTask()
      while (task != null) {
        task.run()
        task = nextTask()
      }
    } while (
      synchronized(lock) {
        if (toRunTrampolined.isEmpty()) {
          scheduledTrampolineDispatch = false
          false
        } else true
      }
    )
  }

  private fun performFrameDispatch(frameTimeNanos: Long) {
    val toRun = synchronized(lock) {
      if (!scheduledFrameDispatch) return
      scheduledFrameDispatch = false
      val result = toRunOnFrame
      toRunOnFrame = spareToRunOnFrame
      spareToRunOnFrame = result
      result
    }
    for (i in 0 until toRun.size) {
      toRun[i].doFrame(frameTimeNanos)
    }
    toRun.clear()
  }

  internal fun postFrameCallback(callback: Choreographer.FrameCallback) {
    synchronized(lock) {
      toRunOnFrame.add(callback)
      if (!scheduledFrameDispatch) {
        scheduledFrameDispatch = true
        choreographer.postFrameCallback(dispatchCallback)
      }
    }
  }

  internal fun removeFrameCallback(callback: Choreographer.FrameCallback) {
    synchronized(lock) {
      toRunOnFrame.remove(callback)
    }
  }

  val frameClock: MonotonicFrameClock = AndroidUiFrameClock(choreographer)

  override fun dispatch(context: CoroutineContext, block: Runnable) {
    synchronized(lock) {
      toRunTrampolined.addLast(block)
      if (!scheduledTrampolineDispatch) {
        scheduledTrampolineDispatch = true
        handler.post(dispatchCallback)
        if (!scheduledFrameDispatch) {
          scheduledFrameDispatch = true
          choreographer.postFrameCallback(dispatchCallback)
        }
      }
    }
  }

  companion object {
    val Main: CoroutineContext by lazy {
      val dispatcher = AndroidUiDispatcher(
        if (isMainThread()) Choreographer.getInstance()
        else runBlocking(Dispatchers.Main) { Choreographer.getInstance() },
        HandlerCompat.createAsync(Looper.getMainLooper())
      )

      dispatcher + dispatcher.frameClock
    }
  }
}

private fun isMainThread() = Looper.myLooper() === Looper.getMainLooper()
