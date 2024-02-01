/*
 * Copyright 2022 Manuel Wrage. Use of this source code is governed by the Apache 2.0 license.
 */

package com.ivianuu.essentials.compose

import androidx.compose.runtime.AbstractApplier
import androidx.compose.runtime.Composable
import androidx.compose.runtime.Composition
import androidx.compose.runtime.MonotonicFrameClock
import androidx.compose.runtime.Recomposer
import androidx.compose.runtime.snapshots.Snapshot
import com.ivianuu.essentials.coroutines.RateLimiter
import com.ivianuu.essentials.time.Clock
import com.ivianuu.injekt.Inject
import com.ivianuu.injekt.Provide
import com.ivianuu.injekt.Tag
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.CoroutineStart
import kotlinx.coroutines.Job
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.channelFlow
import kotlinx.coroutines.job
import kotlinx.coroutines.launch
import kotlin.coroutines.CoroutineContext
import kotlin.coroutines.EmptyCoroutineContext

fun <T> compositionFlow(
  context: CoroutineContext = EmptyCoroutineContext,
  @Inject stateContext: StateCoroutineContext,
  block: @Composable () -> T,
): Flow<T> = channelFlow {
  launchComposition(
    context = context,
    emitter = { trySend(it) },
    block = block
  ).join()
}

fun <T> CoroutineScope.compositionStateFlow(
  context: CoroutineContext = EmptyCoroutineContext,
  @Inject stateContext: StateCoroutineContext,
  block: @Composable () -> T
): StateFlow<T> {
  var flow: MutableStateFlow<T>? = null

  launchComposition(
    context = context,
    emitter = { value ->
      val outputFlow = flow
      if (outputFlow != null) {
        outputFlow.value = value
      } else {
        flow = MutableStateFlow(value)
      }
    },
    block = block,
  )

  return flow!!
}

fun <T> CoroutineScope.launchComposition(
  emitter: (T) -> Unit = {},
  start: CoroutineStart = CoroutineStart.UNDISPATCHED,
  context: CoroutineContext = EmptyCoroutineContext,
  @Inject stateContext: StateCoroutineContext,
  block: @Composable () -> T
): Job = launch(start = start) {
  val finalContext = coroutineContext + stateContext + context
  val recomposer = Recomposer(finalContext)
  val composition = Composition(UnitApplier, recomposer)
  launch(finalContext, CoroutineStart.UNDISPATCHED) {
    recomposer.runRecomposeAndApplyChanges()
  }

  var applyScheduled = false
  val snapshotHandle = Snapshot.registerGlobalWriteObserver {
    if (!applyScheduled) {
      applyScheduled = true
      launch(finalContext) {
        applyScheduled = false
        Snapshot.sendApplyNotifications()
      }
    }
  }

  coroutineContext.job.invokeOnCompletion {
    snapshotHandle.dispose()
    composition.dispose()
  }

  composition.setContent {
    emitter(block())
  }
}

private object UnitApplier : AbstractApplier<Unit>(Unit) {
  override fun insertBottomUp(index: Int, instance: Unit) {}
  override fun insertTopDown(index: Int, instance: Unit) {}
  override fun move(from: Int, to: Int, count: Int) {}
  override fun remove(index: Int, count: Int) {}
  override fun onClear() {}
}

@Tag annotation class StateCoroutineContextTag

typealias StateCoroutineContext = @StateCoroutineContextTag CoroutineContext

@Provide expect object StateCoroutineContextModule {
  @Provide val context: StateCoroutineContext
}

fun RateLimiter.asFrameClock(@Inject clock: Clock): MonotonicFrameClock =
  object : MonotonicFrameClock {
    override suspend fun <R> withFrameNanos(onFrame: (frameTimeNanos: Long) -> R): R {
      acquire()
      return onFrame(clock().inWholeNanoseconds)
    }
  }
