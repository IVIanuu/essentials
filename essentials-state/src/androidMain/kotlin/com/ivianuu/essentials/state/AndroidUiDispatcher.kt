/*
 * Copyright 2022 Manuel Wrage. Use of this source code is governed by the Apache 2.0 license.
 */

package com.ivianuu.essentials.state

import android.os.*
import android.view.*
import androidx.compose.runtime.*
import androidx.core.os.*
import kotlinx.coroutines.*
import kotlin.coroutines.*

actual val StateContext: CoroutineContext = AndroidUiDispatcher.Main

class AndroidUiDispatcher private constructor(
  val choreographer: Choreographer,
  private val handler: Handler
) : CoroutineDispatcher() {

  // Guards all properties in this class
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
    // We don't dispatch holding the lock so that other tasks can get in on our
    // trampolining time slice, but once we're done, make sure nothing added a new task
    // before we set scheduledDispatch = false, which would prevent the next dispatch
    // from being correctly scheduled. Loop to run these stragglers now.
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
      // This callback will not and must not throw, see AndroidUiFrameClock
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

  /**
   * A [MonotonicFrameClock] associated with this [AndroidUiDispatcher]'s [choreographer]
   * that may be used to await [Choreographer] frame dispatch.
   */
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
    /**
     * The [CoroutineContext] containing the [AndroidUiDispatcher] and its [frameClock] for the
     * process's main thread.
     */
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
