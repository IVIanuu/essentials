/*
 * Copyright 2022 Manuel Wrage. Use of this source code is governed by the Apache 2.0 license.
 */

package com.ivianuu.essentials.compose

import androidx.compose.runtime.AbstractApplier
import androidx.compose.runtime.Composable
import androidx.compose.runtime.Composition
import androidx.compose.runtime.MonotonicFrameClock
import androidx.compose.runtime.Recomposer
import androidx.compose.runtime.monotonicFrameClock
import androidx.compose.runtime.snapshots.Snapshot
import com.ivianuu.essentials.coroutines.EventFlow
import com.ivianuu.injekt.Inject
import com.ivianuu.injekt.Provide
import com.ivianuu.injekt.Tag
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.CoroutineStart
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.Runnable
import kotlinx.coroutines.channels.awaitClose
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.channelFlow
import kotlinx.coroutines.isActive
import kotlinx.coroutines.job
import kotlinx.coroutines.launch
import kotlinx.coroutines.suspendCancellableCoroutine
import kotlinx.coroutines.withContext
import kotlin.coroutines.CoroutineContext
import kotlin.coroutines.EmptyCoroutineContext
import kotlin.coroutines.resume

fun <T> compositionFlow(
  @Inject context: StateContext,
  @Inject androidUiDispatcher: @AndroidUiDispatcher CoroutineContext,
  body: @Composable () -> T
): Flow<T> = channelFlow {
  launchComposition(
    emitter = { trySend(it) },
    body = body
  )
  awaitClose()
}

fun <T> CoroutineScope.compositionStateFlow(
  @Inject context: StateContext,
  @Inject androidUiDispatcher: @AndroidUiDispatcher CoroutineContext,
  body: @Composable () -> T
): StateFlow<T> {
  var flow: MutableStateFlow<T>? = null

  launchComposition(
    emitter = { value ->
      val outputFlow = flow
      if (outputFlow != null) {
        outputFlow.value = value
      } else {
        flow = MutableStateFlow(value)
      }
    },
    body = body,
  )

  return flow!!
}

fun <T> CoroutineScope.launchComposition(
  @Inject context: StateContext,
  @Inject androidUiDispatcher: @AndroidUiDispatcher CoroutineContext,
  emitter: (T) -> Unit = {},
  body: @Composable () -> T
) {
  if (!coroutineContext.isActive) return

  var effectContext = coroutineContext + context + androidUiDispatcher.monotonicFrameClock
  val changesApplied = EventFlow<Unit>()
  val delegateDispatcher = effectContext[CoroutineDispatcher]
  effectContext += object : CoroutineDispatcher() {
    override fun dispatch(context: CoroutineContext, block: Runnable) {
      val thiz = this
      launch {
        withContext(androidUiDispatcher) {
          println("wait for changes apply ${Thread.currentThread().name}")
          suspendCancellableCoroutine<Unit> { it.resume(Unit) }
        }

        withContext(
          if (context[CoroutineDispatcher] == thiz) context.plus(delegateDispatcher ?: EmptyCoroutineContext)
          else context
        ) {
          println("changes applied run ${Thread.currentThread().name}")
          block.run()
        }
      }
    }
  }

  val recomposer = Recomposer(effectContext)
  val composition = Composition(NoOpApplier, recomposer)
  launch(context, CoroutineStart.UNDISPATCHED) {
    recomposer.runRecomposeAndApplyChanges()
  }

  var applyScheduled = false
  val snapshotHandle = Snapshot.registerGlobalWriteObserver {
    println("schedule aplpy")
    if (!applyScheduled) {
      applyScheduled = true
      launch(context) {
        println("changes applied")
        applyScheduled = false
        Snapshot.sendApplyNotifications()
        changesApplied.tryEmit(Unit)
      }
    }
  }

  coroutineContext.job.invokeOnCompletion {
    snapshotHandle.dispose()
    composition.dispose()
  }

  composition.setContent {
    println("compose on ${Thread.currentThread().name}")
    emitter(body())
  }
}

@Tag annotation class AndroidUiDispatcher

private object NoOpApplier : AbstractApplier<Unit>(Unit) {
  override fun insertBottomUp(index: Int, instance: Unit) {}
  override fun insertTopDown(index: Int, instance: Unit) {}
  override fun move(from: Int, to: Int, count: Int) {}
  override fun remove(index: Int, count: Int) {}
  override fun onClear() {}
}

@Tag annotation class StateContextTag {
  companion object {
    @Provide val stateContext: StateContext by lazy {
      Dispatchers.Default + ImmediateFrameClock
    }
  }
}

typealias StateContext = @StateContextTag CoroutineContext

object ImmediateFrameClock : MonotonicFrameClock {
  override suspend fun <R> withFrameNanos(onFrame: (frameTimeNanos: Long) -> R): R =
    onFrame(0L)
}
