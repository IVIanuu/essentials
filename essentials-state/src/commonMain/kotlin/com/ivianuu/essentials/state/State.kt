/*
 * Copyright 2021 Manuel Wrage. Use of this source code is governed by the Apache 2.0 license.
 */

package com.ivianuu.essentials.state

import androidx.compose.runtime.AbstractApplier
import androidx.compose.runtime.Composable
import androidx.compose.runtime.Composition
import androidx.compose.runtime.MonotonicFrameClock
import androidx.compose.runtime.MutableState
import androidx.compose.runtime.ProduceStateScope
import androidx.compose.runtime.Recomposer
import androidx.compose.runtime.State
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.produceState
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.snapshotFlow
import androidx.compose.runtime.snapshots.Snapshot
import com.ivianuu.essentials.coroutines.launch
import com.ivianuu.essentials.resource.Idle
import com.ivianuu.essentials.resource.Resource
import com.ivianuu.essentials.resource.flowAsResource
import com.ivianuu.essentials.resource.resourceFlow
import com.ivianuu.injekt.Inject
import com.ivianuu.injekt.Provide
import com.ivianuu.injekt.Spread
import com.ivianuu.injekt.Tag
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.CoroutineStart
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.channelFlow
import kotlinx.coroutines.flow.collect
import kotlinx.coroutines.job
import kotlinx.coroutines.launch
import kotlin.coroutines.CoroutineContext
import kotlin.coroutines.EmptyCoroutineContext

@Tag annotation class ComposedState {
  companion object {
    @Provide @Composable inline fun <@Spread T : @ComposedState State<S>, S> value(
      state: T
    ): S = state.value

    @Provide inline fun <@Spread T : @ComposedState State<S>, S> state(state: T): State<S> = state

    @Provide inline fun <@Spread T : @ComposedState MutableState<S>, S> state(
      state: T
    ): MutableState<S> = state
  }
}

fun <T> composedFlow(body: @Composable () -> T) = channelFlow<T> {
  composedState(
    emitter = { trySend(it).getOrThrow() },
    body = body,
  )
}

fun <T> composedStateFlow(
  @Inject S: CoroutineScope,
  body: @Composable () -> T
): StateFlow<T> {
  var flow: MutableStateFlow<T>? = null

  composedState(
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

expect fun defaultFrameClockContext(): CoroutineContext

fun <T> composedState(
  emitter: (T) -> Unit,
  @Inject scope: CoroutineScope,
  body: @Composable () -> T
) {
  val recomposer = Recomposer(scope.coroutineContext)
  Snapshot.global {
    Composition(UnitApplier, recomposer).run {
      setContent {
        emitter(body())
      }
    }
  }

  launch(
    context = if (scope.coroutineContext[MonotonicFrameClock] == null)
     defaultFrameClockContext() else EmptyCoroutineContext,
    start = CoroutineStart.UNDISPATCHED
  ) {
    recomposer.runRecomposeAndApplyChanges()
  }

  var applyScheduled = false
  val snapshotHandle = Snapshot.registerGlobalWriteObserver {
    if (!applyScheduled) {
      applyScheduled = true
      launch {
        applyScheduled = false
        Snapshot.sendApplyNotifications()
      }
    }
  }

  scope.coroutineContext.job.invokeOnCompletion {
    snapshotHandle.dispose()
  }
}

private object UnitApplier : AbstractApplier<Unit>(Unit) {
  override fun insertBottomUp(index: Int, instance: Unit) {}
  override fun insertTopDown(index: Int, instance: Unit) {}
  override fun move(from: Int, to: Int, count: Int) {}
  override fun remove(index: Int, count: Int) {}
  override fun onClear() {}
}

fun <T> (@Composable () -> T).asComposedFlow(): Flow<T> = composedFlow(body = this)

fun <T> (@Composable () -> T).asComposedStateFlow(@Inject S: CoroutineScope): StateFlow<T> =
  composedStateFlow(body = this)

fun <T> State<T>.asComposedFlow() = snapshotFlow { value }

fun <T> Flow<T>.asComposable(initial: T): @Composable () -> T =
  { collectAsState(initial).value }

fun <T> StateFlow<T>.asComposable(): @Composable () -> T = asComposable(value)

@Composable fun <T> produceValue(
  initialValue: T,
  block: suspend ProduceStateScope<T>.() -> Unit
) = produceState(initialValue, producer = block).value

@Composable fun <T> produceResource(block: suspend () -> T) =
  produceValue<Resource<T>>(Idle) {
    resourceFlow { emit(block()) }.collect { value = it }
  }

@Composable fun <T> valueFromFlow(initial: T, block: () -> Flow<T>): T =
  remember { block() }.collectAsState(initial).value

@Composable fun <T> resourceFromFlow(block: () -> Flow<T>): Resource<T> =
  remember { block().flowAsResource() }.collectAsState(Idle).value

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