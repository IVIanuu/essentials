/*
 * Copyright 2022 Manuel Wrage. Use of this source code is governed by the Apache 2.0 license.
 */

package com.ivianuu.essentials.state

import androidx.compose.runtime.AbstractApplier
import androidx.compose.runtime.Composable
import androidx.compose.runtime.Composition
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.MonotonicFrameClock
import androidx.compose.runtime.Recomposer
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.runtime.snapshots.Snapshot
import com.ivianuu.essentials.resource.Idle
import com.ivianuu.essentials.resource.Resource
import com.ivianuu.essentials.resource.flowAsResource
import com.ivianuu.essentials.resource.resourceFlow
import com.ivianuu.injekt.Provide
import com.ivianuu.injekt.Tag
import com.ivianuu.injekt.inject
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.CoroutineStart
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.channels.awaitClose
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.channelFlow
import kotlinx.coroutines.isActive
import kotlinx.coroutines.job
import kotlinx.coroutines.launch
import kotlin.coroutines.CoroutineContext

context(StateContext) fun <T> stateFlow(body: @Composable () -> T): Flow<T> = channelFlow {
  launchMolecule(
    emitter = { trySend(it).getOrThrow() },
    body = body
  )
  awaitClose()
}

context(CoroutineScope, StateContext) fun <T> state(body: @Composable () -> T): StateFlow<T> {
  var flow: MutableStateFlow<T>? = null

  launchMolecule(
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

context(CoroutineScope, StateContext) fun <T> launchMolecule(
  emitter: (T) -> Unit,
  body: @Composable () -> T
) {
  if (!coroutineContext.isActive) return

  val context = inject<StateContext>()

  val recomposer = Recomposer(coroutineContext + context)
  val composition = Composition(UnitApplier, recomposer)
  launch(context, CoroutineStart.UNDISPATCHED) {
    recomposer.runRecomposeAndApplyChanges()
  }

  var applyScheduled = false
  val snapshotHandle = Snapshot.registerGlobalWriteObserver {
    if (!applyScheduled) {
      applyScheduled = true
      launch(context) {
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

@Tag annotation class StateContextTag {
  companion object {
    @Provide val stateContext: StateContext by lazy {
      Dispatchers.Main + immediateFrameClock()
    }
  }
}

typealias StateContext = @StateContextTag CoroutineContext

private fun immediateFrameClock() = object : MonotonicFrameClock {
  override suspend fun <R> withFrameNanos(onFrame: (frameTimeNanos: Long) -> R): R =
    onFrame(0L)
}

@Composable fun <T> Flow<T>.bind(initial: T, vararg args: Any?): T {
  val state = remember(*args) { mutableStateOf(initial) }

  LaunchedEffect(state) {
    collect { state.value = it }
  }

  return state.value
}

@Composable fun <T> StateFlow<T>.bind(vararg args: Any?): T = bind(initial = value, args = *args)

@Composable fun <T> Flow<T>.bindResource(vararg args: Any?): Resource<T> =
  flowAsResource().bind(initial = Idle, args = *args)

interface ProduceScope<T> : CoroutineScope {
  var value: T
}

@Composable fun <T> produce(
  initial: T,
  vararg args: Any?,
  block: suspend ProduceScope<T>.() -> Unit
): T {
  val state = remember(*args) { mutableStateOf(initial) }

  LaunchedEffect(state) {
    block(
      object : ProduceScope<T>, CoroutineScope by this {
        override var value by state
      }
    )
  }

  return state.value
}

@Composable fun <T> produceResource(
  vararg args: Any?,
  block: suspend () -> T
): Resource<T> = remember(*args) {
  resourceFlow { emit(block()) }
}.bind(Idle)

@Composable fun action(block: suspend () -> Unit): () -> Unit {
  val scope = rememberCoroutineScope()
  return {
    scope.launch {
      block()
    }
  }
}

@Composable fun <P1> action(block: suspend (P1) -> Unit): (P1) -> Unit {
  val scope = rememberCoroutineScope()
  return { p1 ->
    scope.launch {
      block(p1)
    }
  }
}

@Composable fun <P1, P2> action(block: suspend (P1, P2) -> Unit): (P1, P2) -> Unit {
  val scope = rememberCoroutineScope()
  return { p1, p2 ->
    scope.launch {
      block(p1, p2)
    }
  }
}

@Composable fun <P1, P2, P3> action(block: suspend (P1, P2, P3) -> Unit): (P1, P2, P3) -> Unit {
  val scope = rememberCoroutineScope()
  return { p1, p2, p3 ->
    scope.launch {
      block(p1, p2, p3)
    }
  }
}

@Composable fun <P1, P2, P3, P4> action(block: suspend (P1, P2, P3, P4) -> Unit): (P1, P2, P3, P4) -> Unit {
  val scope = rememberCoroutineScope()
  return { p1, p2, p3, p4 ->
    scope.launch {
      block(p1, p2, p3, p4)
    }
  }
}

@Composable fun <P1, P2, P3, P4, P5> action(block: suspend (P1, P2, P3, P4, P5) -> Unit): (P1, P2, P3, P4, P5) -> Unit {
  val scope = rememberCoroutineScope()
  return { p1, p2, p3, p4, p5 ->
    scope.launch {
      block(p1, p2, p3, p4, p5)
    }
  }
}
