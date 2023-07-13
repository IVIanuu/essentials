/*
 * Copyright 2022 Manuel Wrage. Use of this source code is governed by the Apache 2.0 license.
 */

package com.ivianuu.essentials.compose

import androidx.compose.runtime.AbstractApplier
import androidx.compose.runtime.Composable
import androidx.compose.runtime.Composition
import androidx.compose.runtime.Recomposer
import androidx.compose.runtime.snapshots.Snapshot
import com.ivianuu.injekt.Provide
import com.ivianuu.injekt.Tag
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.CoroutineStart
import kotlinx.coroutines.Job
import kotlinx.coroutines.channels.awaitClose
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.channelFlow
import kotlinx.coroutines.job
import kotlinx.coroutines.launch
import kotlin.coroutines.CoroutineContext

context(StateCoroutineContext)
fun <T> compositionFlow(body: @Composable () -> T): Flow<T> = channelFlow {
  launchComposition(
    emitter = { trySend(it) },
    body = body
  )
  awaitClose()
}

context(StateCoroutineContext)
fun <T> CoroutineScope.compositionStateFlow(
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

context(StateCoroutineContext)
fun <T> CoroutineScope.launchComposition(
  emitter: (T) -> Unit = {},
  body: @Composable () -> T
): Job = launch(start = CoroutineStart.UNDISPATCHED) {
  val recomposer = Recomposer(coroutineContext + this@StateCoroutineContext)
  val composition = Composition(UnitApplier, recomposer)
  launch(this@StateCoroutineContext, CoroutineStart.UNDISPATCHED) {
    recomposer.runRecomposeAndApplyChanges()
  }

  var applyScheduled = false
  val snapshotHandle = Snapshot.registerGlobalWriteObserver {
    if (!applyScheduled) {
      applyScheduled = true
      launch(this@StateCoroutineContext) {
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
    emitter(body())
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
