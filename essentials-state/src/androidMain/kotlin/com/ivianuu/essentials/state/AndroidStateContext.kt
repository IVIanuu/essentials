/*
 * Copyright 2022 Manuel Wrage. Use of this source code is governed by the Apache 2.0 license.
 */

package com.ivianuu.essentials.state

import android.os.*
import android.view.*
import androidx.compose.runtime.*
import androidx.core.os.*
import com.ivianuu.injekt.*
import kotlinx.coroutines.*
import kotlin.coroutines.*

actual object StateContextInjectables {
  @Provide actual val context: StateContext by lazy {
    val dispatcher = AndroidUiDispatcher(
      if (isMainThread()) Choreographer.getInstance()
      else runBlocking(Dispatchers.Main) { Choreographer.getInstance() },
      HandlerCompat.createAsync(Looper.getMainLooper())
    )

    dispatcher + dispatcher.frameClock
  }
}

class AndroidUiDispatcher internal constructor(
  val choreographer: Choreographer,
  private val handler: Handler
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
}

private fun isMainThread() = Looper.myLooper() === Looper.getMainLooper()

class AndroidUiFrameClock(private val choreographer: Choreographer) : MonotonicFrameClock {
  override suspend fun <R> withFrameNanos(
    onFrame: (Long) -> R
  ): R {
    val uiDispatcher = coroutineContext[ContinuationInterceptor] as? AndroidUiDispatcher
    return suspendCancellableCoroutine { co ->
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
